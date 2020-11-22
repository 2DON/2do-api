package io.github._2don.api.step;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.task.TaskJPA;
import io.github._2don.api.task.TaskService;
import io.github._2don.api.utils.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static io.github._2don.api.projectmember.ProjectMemberPermission.MAN_TASKS;

@Service
public class StepService {

  @Autowired
  private AccountService accountService;
  @Autowired
  private StepJPA stepJPA;
  @Autowired
  private TaskService taskService;
  @Autowired
  private ProjectMemberService projectMemberService;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private TaskJPA taskJPA;

  public List<StepDTO> findSteps(@NonNull Long accountId,
                                 @NonNull Long projectId,
                                 @NonNull Long taskId) {
    projectMemberService.assertIsMember(accountId, projectId, HttpStatus.UNAUTHORIZED); // project and account exists and the account is part of the project
    taskService.assertExists(projectId, taskId); // task exists and is part of the project

    return stepJPA
      .findAllByTaskId(taskId, Sort.by(Sort.Direction.ASC, "ordinal"))
      .stream()
      .map(StepDTO::new)
      .collect(Collectors.toList());
  }

  public StepDTO create(@NonNull Long accountId,
                        @NonNull Long projectId,
                        @NonNull Long taskId,
                        @NonNull String description,
                        Integer ordinal) {
    var member = projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_TASKS);
    taskService.assertExists(projectId, taskId);

    if (description.length() < 1 || description.length() >= 80) {
      throw Status.BAD_REQUEST.get();
    }

    var step = new Step()
      .setTask(taskJPA.getOne(taskId))
      .setOrdinal(ordinal == null ? Integer.MAX_VALUE : ordinal)
      .setDescription(description)
      .setCreatedBy(member.getAccount())
      .setUpdatedBy(member.getAccount());

    return new StepDTO(stepJPA.save(step));
  }

  public StepDTO update(@NonNull Long accountId,
                        @NonNull Long projectId,
                        @NonNull Long taskId,
                        @NonNull Long stepId,
                        String description,
                        StepStatus status,
                        Integer ordinal,
                        String observation) {
    var member = projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_TASKS);
    var step = stepJPA.findByIdAndTaskIdAndTaskProjectId(stepId, taskId, projectId).orElseThrow(Status.NOT_FOUND);

    if (description != null) {
      if (description.length() < 1 || description.length() >= 80) {
        throw Status.BAD_REQUEST.get();
      }
      step.setDescription(description);
    }

    if (ordinal != null) {
      step.setOrdinal(ordinal);
    }

    if (status != null) {
      step.setStatus(status);
    }

    if (observation != null) {
      if (observation.isBlank()) {
        step.setObservation(null);
      }
      if (step.getDescription().length() > 250) {
        throw Status.BAD_REQUEST.get();
      }
      step.setObservation(observation);
    }

    step.setUpdatedBy(member.getAccount());

    step = stepJPA.save(step);
    return new StepDTO(step);
  }

  public void delete(@NonNull Long accountId,
                     @NonNull Long projectId,
                     @NonNull Long taskId,
                     @NonNull Long stepId) {
    projectMemberService.findIfIsMemberAndHavePermission(accountId, projectId, MAN_TASKS);

    var step = stepJPA.findByIdAndTaskIdAndTaskProjectId(stepId, taskId, projectId).orElseThrow(Status.NOT_FOUND);

    stepJPA.delete(step);
  }

}
