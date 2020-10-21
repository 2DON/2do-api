package io.github._2don.api.controllers;

import io.github._2don.api.models.Project;
import io.github._2don.api.models.ProjectMembers;
import io.github._2don.api.models.ProjectMembersPermissions;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.ProjectJPA;
import io.github._2don.api.repositories.ProjectMembersJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
  private ProjectMembersJPA projectMembersJPA;

  @GetMapping
  public List<Project> index(@AuthenticationPrincipal Long accountId,
                             @RequestParam(value = "archived", required = false, defaultValue = "false") Boolean archived) {
    return projectMembersJPA
      .findAllByAccountId(accountId)
      .stream()
      .map(ProjectMembers::getProject)
      .filter(project -> project.getArchived() == archived)
      .sorted(Comparator.comparingInt(Project::getOrdinal))
      .collect(Collectors.toList());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Project store(@AuthenticationPrincipal Long accountId,
                       @Validated @RequestBody Project project) {
    var account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (!account.getPremium() && projectMembersJPA.existsByAccountId(accountId)) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }

    project.setCreatedBy(account);
    project.setUpdatedBy(account);

    project = projectJPA.save(project);

    projectMembersJPA.save(new ProjectMembers(
      account,
      project,
      null,
      ProjectMembersPermissions.ALL));

    return project;
  }

  @GetMapping("/{projectId}")
  public Project show(@AuthenticationPrincipal Long accountId,
                      @PathVariable("id") Long projectId) {
    if (!projectMembersJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return projectJPA.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  @PatchMapping("/{projectId}")
  public Project edit(@AuthenticationPrincipal Long accountId,
                      @PathVariable("id") Long projectId,
                      @RequestBody Project project) {
    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MANAGE) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var projectEdit = projectJPA.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    // project is archived and the request don't unarchive it
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
                      @PathVariable("projectId") Long projectId) {
    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.ALL) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO delete project + members + tasks + steps
    // TODO set delete cascade on tasks and steps
    projectJPA.delete(projectJPA.getOne(projectId));
  }
}
