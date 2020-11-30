package io.github._2don.api.project;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermission;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.step.StepJPA;
import io.github._2don.api.task.Task;
import io.github._2don.api.task.TaskJPA;
import io.github._2don.api.utils.ImageEncoder;
import io.github._2don.api.utils.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static io.github._2don.api.projectmember.ProjectMemberPermission.MAN_PROJECT;
import static io.github._2don.api.projectmember.ProjectMemberPermission.OWNER;

@Service
public class ProjectService {

  private final long NON_PREMIUM_OWN_PROJECTS_LIMIT;

  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private ProjectMemberService projectMemberService;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private StepJPA stepJPA;

  public ProjectService(@Value("${non-premium-limits.own-projects}") long nonPremiumOwnProjectsLimit) {
    this.NON_PREMIUM_OWN_PROJECTS_LIMIT = nonPremiumOwnProjectsLimit;
  }

  public void assertProjectLimit(@NonNull Long accountId) {
    assertProjectLimit(accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND));
  }

  public void assertProjectLimit(@NonNull Account account) {
    if (!account.getPremium()
      && projectMemberJPA.countByAccountIdAndPermission(account.getId(), OWNER) >= NON_PREMIUM_OWN_PROJECTS_LIMIT) {
      throw Status.UPGRADE_REQUIRED.get();
    }
  }

  public List<ProjectDTO> findProjects(@NonNull Long accountId,
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

    assertProjectLimit(accountId);

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
      .findIfIsMemberAndHavePermission(accountId, projectId, ProjectMemberPermission.MAN_PROJECT);

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

  public ProjectDTO updateIcon(Long accountId, Long projectId, MultipartFile icon) {
    var member = projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_PROJECT);

    var project = member.getProject();

    if (icon == null || icon.isEmpty()) {
      project
        .setIcon(null)
        .setUpdatedBy(member.getAccount());
    } else {
      if (!ImageEncoder.supports(icon.getContentType())) {
        throw Status.BAD_REQUEST.get();
      }

      try {
        project
          .setIcon(ImageEncoder.encodeToString(icon.getBytes()))
          .setUpdatedBy(member.getAccount());
      } catch (IOException e) {
        throw Status.BAD_REQUEST.get();
      }
    }

    return new ProjectDTO(projectJPA.save(project), member.getPermission());
  }

  public void transferOwnership(@NonNull Long ownerId,
                                @NonNull Long projectId,
                                @NonNull Long newOwnerId) {
    if (ownerId.equals(newOwnerId)) {
      throw Status.BAD_REQUEST.get();
    }

    var owner = projectMemberService.findIfIsMemberAndHavePermission(ownerId, projectId, OWNER);
    var newOwner = projectMemberService.findIfIsMember(newOwnerId, projectId);

    assertProjectLimit(newOwner.getAccount()); // new owner is premium or have not reached the project limit

    owner
      .setPermission(MAN_PROJECT)
      .setUpdatedBy(owner.getAccount());

    newOwner
      .setPermission(OWNER)
      .setUpdatedBy(owner.getAccount());

    projectMemberJPA.save(owner);
    projectMemberJPA.save(newOwner);
  }

  public ProjectDTO toggleArchived(@NonNull Long accountId,
                                   @NonNull Long projectId) {
    var member = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(Status.NOT_FOUND);

    if (!member.isOwner()) {
      throw Status.UNAUTHORIZED.get();
    }

    var project = member.getProject();

    project
      .setArchived(!project.isArchived())
      .setUpdatedBy(member.getAccount());

    project = projectJPA.save(project);
    return new ProjectDTO(project, member.getPermission());
  }

  public void delete(Long accountId,
                     Long projectId) {
    var member = projectMemberService
      .findIfIsMemberAndHavePermission(accountId, projectId, ProjectMemberPermission.OWNER);

    // query all taskIds for this project
    var taskIds = taskJPA.findAllByProjectId(projectId, Sort.unsorted()).stream().mapToLong(Task::getId);

    // delete all steps
    taskIds.forEach(taskId -> {
      stepJPA.deleteByTaskId(taskId);
    });

    // delete all tasks
    taskJPA.deleteByProjectId(projectId);

    // remove all members
    projectMemberJPA.deleteByProjectId(projectId);

    // delete the project
    projectJPA.delete(member.getProject());
  }

}
