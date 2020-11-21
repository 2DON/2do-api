package io.github._2don.api.project;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermission;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.utils.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private ProjectMemberService projectMemberService;
  @Autowired
  private AccountJPA accountJPA;

  public List<ProjectDTO> listByAccountId(@NonNull Long accountId,
                                          boolean archived) {
    return projectMemberJPA
      .findAllByAccountIdAndProjectArchived(accountId, archived)
      .stream()
      .map(ProjectDTO::new)
      .collect(Collectors.toList());
  }

  public ProjectDTO find(Long accountId, Long projectId) {
    return projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .map(ProjectDTO::new)
      .orElseThrow(Status.UNAUTHORIZED);
  }

  public ProjectDTO create(@NonNull Long accountId,
                           @NonNull String description,
                           String observation,
                           Integer ordinal) {
    if (description.length() <= 1 || description.getBytes().length > 1024) {
      throw Status.BAD_REQUEST.get();
    }

    projectMemberService.assertProjectLimit(accountId);

    var project = new Project()
      .setDescription(description)
      .setOrdinal(ordinal == null ? Integer.MAX_VALUE : ordinal);

    if (observation != null) {
      if (observation.isBlank()) {
        project.setObservation(null);
      } else if (observation.length() <= 1 || observation.length() > 250) {
        throw Status.BAD_REQUEST.get();
      } else {
        project.setObservation(observation);
      }
    }

    var account = accountJPA.getOne(accountId);
    project
      .setCreatedBy(account)
      .setUpdatedBy(account);
    project = projectJPA.save(project);

    projectMemberJPA.save(new ProjectMember()
      .setAccountId(accountId)
      .setProjectId(project.getId())
      .setPermission(ProjectMemberPermission.OWNER)
      .setCreatedBy(account)
      .setUpdatedBy(account));

    return new ProjectDTO(project, ProjectMemberPermission.OWNER);
  }

  public ProjectDTO update(@NonNull Long accountId,
                           @NonNull Long projectId,
                           String description,
                           String observation,
                           Integer ordinal,
                           String options) {
    var member = projectMemberService
      .findIfHavePermission(accountId, projectId, ProjectMemberPermission.MAN_PROJECT);

    var project = member.getProject();

    if (description != null) {
      if (description.length() <= 1 || description.getBytes().length > 1024) {
        throw Status.BAD_REQUEST.get();
      }
      project.setDescription(description);
    }

    if (observation != null) {
      if (observation.isBlank()) {
        project.setObservation(null);
      } else if (observation.length() <= 1 || observation.length() > 250) {
        throw Status.BAD_REQUEST.get();
      } else {
        project.setObservation(observation);
      }
    }

    if (ordinal != null) {
      project.setOrdinal(ordinal);
    }

    if (options != null) {
      project.setOptions(options.isBlank() ? null : options);
    }

    project.setUpdatedBy(member.getAccount());
    project = projectJPA.save(project);
    return new ProjectDTO(project, member.getPermission());
  }

  public ProjectDTO toggleArchiving(@NonNull Long accountId,
                                    @NonNull Long projectId) {
    var member = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(Status.NOT_FOUND);

    if (member.isNot(ProjectMemberPermission.OWNER)) {
      throw Status.UNAUTHORIZED.get();
    }

    var project = member.getProject();

    project
      .setArchived(!project.getArchived())
      .setUpdatedBy(member.getAccount());

    project = projectJPA.save(project);
    return new ProjectDTO(project, member.getPermission());
  }

  public void delete(Long accountId,
                     Long projectId) {
    var member = projectMemberService
      .findIfHavePermission(accountId, projectId, ProjectMemberPermission.OWNER);

    // TODO delete project + members + tasks + steps
    // TODO set delete cascade on tasks and steps
    // TODO backup project for X time
    projectJPA.delete(member.getProject());
  }


}
