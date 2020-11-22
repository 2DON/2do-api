package io.github._2don.api.task;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.utils.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static io.github._2don.api.projectmember.ProjectMemberPermission.MAN_MEMBERS;
import static io.github._2don.api.projectmember.ProjectMemberPermission.MAN_TASKS;

@Service
public class TaskService {

  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private AccountService accountService;
  @Autowired
  private ProjectMemberService projectMemberService;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;

  public void assertExists(@NonNull Long projectId,
                           @NonNull Long taskId) {
    if (!taskJPA.existsByIdAndProjectId(taskId, projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  public List<TaskDTO> findTasks(@NotNull Long accountId,
                                 @NotNull Long projectId) {
    projectMemberService.assertIsMember(accountId, projectId, HttpStatus.UNAUTHORIZED);

    return taskJPA
      .findAllByProjectId(projectId, Sort.by(Sort.Direction.ASC, "ordinal"))
      .stream()
      .map(TaskDTO::new)
      .collect(Collectors.toList());
  }

  public TaskDTO create(@NonNull Long accountId,
                        @NonNull Long projectId,
                        @NonNull String description,
                        Integer ordinal) {
    var member = projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_MEMBERS);

    if (description.length() < 1 || description.length() >= 80) {
      throw Status.BAD_REQUEST.get();
    }

    var task = new Task()
      .setDescription(description)
      .setOrdinal(ordinal == null ? Integer.MAX_VALUE : ordinal)
      .setCreatedBy(member.getAccount())
      .setUpdatedBy(member.getAccount())
      .setProject(member.getProject())
      .setAssignedTo(null);

    return new TaskDTO(taskJPA.save(task));
  }

  public TaskDTO update(@NonNull Long accountId,
                        @NonNull Long projectId,
                        @NonNull Long taskId,
                        String description,
                        Integer ordinal,
                        TaskStatus status,
                        String options,
                        Long assignedTo) {
    var member = projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_MEMBERS);

    var task = taskJPA.findByIdAndProjectId(taskId, projectId).orElseThrow(Status.NOT_FOUND);

    if (description != null) {
      if (description.length() < 1 || description.length() >= 80) {
        throw Status.BAD_REQUEST.get();
      }
      task.setDescription(task.getDescription());
    }

    if (ordinal != null) {
      task.setOrdinal(ordinal);
    }

    if (status != null) {
      task.setStatus(status);
    }

    if (options != null) {
      if (options.isBlank()) {
        task.setOptions(null);
      }
      task.setOptions(options);
    }

    if (assignedTo != null) {
      projectMemberService.assertIsMember(assignedTo, projectId, HttpStatus.UNAUTHORIZED);

      task.setAssignedTo(accountJPA.getOne(assignedTo));
    }

    task.setUpdatedBy(member.getAccount());

    return new TaskDTO(taskJPA.save(task));
  }

  public void delete(@NonNull Long accountId,
                     @NonNull Long projectId,
                     @NonNull Long taskId) {
    projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_TASKS);

    var task = taskJPA.findByIdAndProjectId(taskId, projectId).orElseThrow(Status.NOT_FOUND);

    taskJPA.delete(task);
  }

}
