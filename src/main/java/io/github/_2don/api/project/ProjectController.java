package io.github._2don.api.project;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;

  @GetMapping
  public List<Project> index(@AuthenticationPrincipal Long accountId,
                             @RequestParam(value = "archived", required = false, defaultValue = "false") Boolean archived) {

    return projectMemberJPA
      .findAllByAccountId(accountId)
      .stream()
      .map(ProjectMember::getProject)
      .filter(project -> project.getArchived() == archived)
      .sorted(Comparator.comparingInt(Project::getOrdinal))
      .collect(Collectors.toList());
  }

  // fix
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Project store(@AuthenticationPrincipal Long accountId,
                       @RequestBody Project project) {

    var account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE));

    if (!account.getPremium()
      && projectMemberJPA.existsByAccountId(accountId)) {
      // non-premium accounts can have only one project
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }

    project.setCreatedBy(account);
    project.setUpdatedBy(account);

    project = projectJPA.save(project);

    projectMemberJPA.save(new ProjectMember()
      .setAccount(account)
      .setProject(project)
      .setPermissions(ProjectMemberPermissions.OWNER));

    return project;
  }

  @GetMapping("/{projectId}")
  public Project show(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long projectId) {

    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    return projectMeta.getProject();
  }

  @PatchMapping("/{projectId}")
  public Project edit(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long projectId,
                      @RequestBody Project project) {

    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElse(null);
    if (projectMeta == null
      || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_PROJECT) < 0) {
      // not enough permission
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var projectEdit = projectMeta.getProject();

    // project is archived and the request don't unarchive it
    // TODO archiving deserves his own route?
    if (projectEdit.getArchived() && (project.getArchived() == null || !project.getArchived())) {
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }

    if (project.getArchived() != null) {
      projectEdit.setArchived(project.getArchived());
    }

    if (project.getDescription() != null) {
      if (project.getDescription().length() == 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      projectEdit.setDescription(project.getDescription());
    }

    if (projectEdit.getObservation() != null) {
      if (projectEdit.getObservation().length() == 0) {
        project.setObservation(null);
      } else {
        project.setObservation(projectEdit.getObservation());
      }
    }

    if (projectEdit.getOptions() != null) {
      if (projectEdit.getOptions().length() == 0) {
        project.setOptions(null);
      } else {
        project.setOptions(projectEdit.getOptions());
      }
    }

    projectEdit.setUpdatedBy(accountJPA.getOne(accountId));

    return projectJPA.save(projectEdit);
  }

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long projectId) {

    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElse(null);
    if (projectMeta == null
      || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_PROJECT) < 0) {
      // not enough permission
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO delete project + members + tasks + steps
    // TODO set delete cascade on tasks and steps
    // TODO backup project for X time
    projectJPA.delete(projectJPA.getOne(projectId));
  }
}
