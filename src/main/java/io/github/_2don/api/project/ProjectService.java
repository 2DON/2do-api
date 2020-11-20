package io.github._2don.api.project;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import io.github._2don.api.projectmember.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountService accountService;
  @Autowired
  private ProjectMemberService projectMemberService;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;

  // FIXME
  public Project add(Long accountId, Project project) {

    var account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    projectMemberService.assertProjectLimit(accountId);

    project.setCreatedBy(account);
    project.setUpdatedBy(account);

    project = projectJPA.save(project);

    projectMemberJPA.save(new ProjectMember().setAccountId(accountId).setProjectId(project.getId())
      .setPermissions(ProjectMemberPermissions.OWNER).setCreatedBy(account)
      .setUpdatedBy(account));

    return project;
  }

  public Project update(Long accountId, Long oldProjectId, Project project) {

    var projectMember =
      projectMemberService.getProjectMember(accountId, oldProjectId).orElse(null);

    if (projectMember == null
      || projectMember.getPermissions().compareTo(ProjectMemberPermissions.MAN_PROJECT) < 0) {
      // not enough permission
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var projectEdit = projectMember.getProject();

    // project is archived and the request don't unarchive it
    // TODO archiving deserves his own route?
    if (projectEdit.getArchived() && (project.getArchived() == null || !project.getArchived())) {
      throw Status.LOCKED.get();
    }

    if (project.getArchived() != null) {
      projectEdit.setArchived(project.getArchived());
    }

    if (project.getDescription() != null) {
      if (project.getDescription().length() == 0) {
        throw Status.BAD_REQUEST.get();
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

    projectEdit.setUpdatedBy(projectMeta.getAccount());

    return projectJPA.save(projectEdit);
  }

  public void delete(Long accountId,
                     Long projectId) {
    var projectMember = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(Status.NOT_FOUND);

    if (projectMember == null
      || projectMember.getPermissions().compareTo(ProjectMemberPermissions.MAN_PROJECT) < 0) {
      // not enough permission
      throw Status.UNAUTHORIZED.get();
    }

    // TODO delete project + members + tasks + steps
    // TODO set delete cascade on tasks and steps
    // TODO backup project for X time
    projectJPA.delete(projectJPA.getOne(projectId));
  }

  public Project getProject(Long accountId, Long projectId) {

    if (!projectMemberService.exist(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return projectJPA.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  public List<Project> getAllProjectByAccountId(Long accountId, boolean archived) {
    return projectMemberService.getAllProjectMembers(accountId).stream()
      .map(ProjectMember::getProject).filter(project -> project.getArchived() == archived)
      .sorted(Comparator.comparingInt(Project::getOrdinal)).collect(Collectors.toList());
  }

  public List<Project> getAllProjectByAccountId(Long accountId) {
    return projectMemberService.getAllProjectMembers(accountId).stream()
      .map(ProjectMember::getProject).sorted(Comparator.comparingInt(Project::getOrdinal))
      .collect(Collectors.toList());
  }


}
