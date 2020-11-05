package io.github._2don.api.project;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import io.github._2don.api.projectmember.ProjectMemberService;

@Service
public class ProjectService {

  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountService accountService;
  @Autowired
  private ProjectMemberService projectMemberService;

  // fix
  public Project add(Long accountId, Project project) {

    Account account = accountService.getAccount(accountId);

    if (!account.getPremium() && projectMemberService.exist(project)) {
      // non-premium accounts can have only one project
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }

    project.setCreatedBy(account);
    project.setUpdatedBy(account);

    project = projectJPA.save(project);

    projectMemberService.add(project.getCreatedBy(), project, ProjectMemberPermissions.OWNER);

    return project;
  }

  public Project update(Long accountId, Long oldProjectId, Project project) {

    ProjectMember projectMember =
        projectMemberService.getProjectMember(accountId, oldProjectId).orElse(null);

    if (projectMember == null
        || projectMember.getPermissions().compareTo(ProjectMemberPermissions.MAN_PROJECT) < 0) {
      // not enough permission
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    Project projectEdit = projectMember.getProject();

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

    projectEdit.setUpdatedBy(accountService.getAccount(accountId));

    return projectJPA.save(projectEdit);
  }

  public void delete(Long accountId, Long projectId) {

    ProjectMember projectMember =
        projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMember == null
        || projectMember.getPermissions().compareTo(ProjectMemberPermissions.MAN_PROJECT) < 0) {
      // not enough permission
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO delete project + members + tasks + steps
    // TODO set delete cascade on tasks and steps
    // TODO backup project for X time
    projectJPA.delete(projectJPA.getOne(projectId));
  }

  public Project getProject(Long accountId, Long projectId) {

    Account account = accountService.getAccount(accountId);
    Project project = projectJPA.getOne(projectId);

    boolean hasProject = projectMemberService.hasProject(account, project);

    if (!hasProject) {
      new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return projectJPA.getOne(projectId);
  }

  public List<Project> getAllProjectByAccountId(Long accountId, boolean archived) {
    return projectMemberService.getAllProjectMembers(accountId).stream()
        .map(ProjectMember::getProject).filter(project -> project.getArchived() == archived)
        .sorted(Comparator.comparingInt(Project::getOrdinal)).collect(Collectors.toList());
  }



}
