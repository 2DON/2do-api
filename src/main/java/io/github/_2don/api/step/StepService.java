package io.github._2don.api.step;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermission;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.task.TaskService;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

  public Step edit(Long accountId, Long stepId, Long projectId, Long taskId, Step step) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectIdAndProjectArchived(accountId, projectId, false)
      .orElseThrow(Status.UNAUTHORIZED);

    if (projectMeta.canNot(ProjectMemberPermission.MAN_PROJECT)) {
      throw Status.UNAUTHORIZED.get();
    }

    // step exists and belongs to task and project
    var stepEdit = stepJPA.findById(stepId).orElse(null);
    if (stepEdit == null || !stepEdit.getTask().getId().equals(taskId)
      || !stepEdit.getTask().getProject().getId().equals(projectId)) {
      throw Status.BAD_REQUEST.get();
    }

    if (step.getDescription() != null) {
      if (step.getDescription().length() == 0 || step.getDescription().length() >= 80) {
        throw Status.BAD_REQUEST.get();
      }
      stepEdit.setDescription(step.getDescription());
    }

    if (step.getOrdinal() != null) {
      stepEdit.setOrdinal(step.getOrdinal());
    }

    if (step.getStatus() != null) {
      stepEdit.setStatus(step.getStatus());
    }

    if (step.getObservation() != null) {
      if (step.getObservation().length() == 0) {
        stepEdit.setObservation(null);
      }
      if (step.getDescription().length() > 250) {
        throw Status.BAD_REQUEST.get();
      }
      stepEdit.setObservation(step.getObservation());
    }

    stepEdit.setUpdatedBy(projectMeta.getAccount());

    return stepJPA.save(stepEdit);
  }

  public Step edit(Long accountId, Long stepId, Long projectId, Long taskId, Integer ordinal,
                   String description, String observation, String status) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectIdAndProjectArchived(accountId, projectId, false)
      .orElseThrow(Status.UNAUTHORIZED);

    if (projectMeta.canNot(ProjectMemberPermission.MAN_PROJECT)) {
      throw Status.UNAUTHORIZED.get();
    }

    // step exists and belongs to task and project
    var stepEdit = stepJPA.findById(stepId).orElse(null);
    if (stepEdit == null || !stepEdit.getTask().getId().equals(taskId)
      || !stepEdit.getTask().getProject().getId().equals(projectId)) {
      throw Status.BAD_REQUEST.get();
    }

    if (description != null) {
      if (description.length() == 0 || description.length() >= 80) {
        throw Status.BAD_REQUEST.get();
      }
      stepEdit.setDescription(description);
    }

    if (ordinal != null) {
      stepEdit.setOrdinal(ordinal);
    }

    if (status != null) {
      var stepStatus = StepStatus.valueOf(status);
      stepEdit.setStatus(stepStatus);
    }

    if (observation != null) {
      // FIXME #topmeme2020
      if (observation.length() < 0 || observation.length() >= 250) {
        throw Status.BAD_REQUEST.get();
      }

      stepEdit.setObservation(observation);
    }

    stepEdit.setUpdatedBy(projectMeta.getAccount());

    return stepJPA.save(stepEdit);
  }

  public Step add(Long accountId, Step step, Long projectId, Long taskId) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectIdAndProjectArchived(accountId, projectId, false)
      .orElseThrow(Status.UNAUTHORIZED);

    if (projectMeta.canNot(ProjectMemberPermission.MAN_PROJECT)) {
      throw Status.UNAUTHORIZED.get();
    }

    // task exists and belongs to project
    var task = taskService.getTask(taskId, projectId);

    step
      .setCreatedBy(projectMeta.getAccount())
      .setUpdatedBy(projectMeta.getAccount())
      .setTask(task);

    return stepJPA.save(step);
  }

  public Step add(Long accountId, String description, Long projectId, Long taskId) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectIdAndProjectArchived(accountId, projectId, false)
      .orElseThrow(Status.UNAUTHORIZED);

    if (projectMeta.canNot(ProjectMemberPermission.MAN_PROJECT)) {
      throw Status.UNAUTHORIZED.get();
    }

    // task exists and belongs to project
    var task = taskService.getTask(projectId, taskId);

    var account = accountJPA.findById(accountId);

    Step step = new Step();
    step.setCreatedBy(projectMeta.getAccount());
    step.setUpdatedBy(projectMeta.getAccount());
    step.setTask(task);
    step.setDescription(description);

    return stepJPA.save(step);
  }

  public List<Step> getSteps(Long accountId, Long projectId, Long taskId) {
    // account is member of project
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw Status.UNAUTHORIZED.get();
    }

    // task exists and belongs to project
    if (!taskService.exist(projectId, taskId)) {
      throw Status.BAD_REQUEST.get();
    }

    return stepJPA.findAllByTaskId(taskId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  public Step getStep(Long accountId, Long stepId, Long projectId, Long taskId) {
    // account is member of project
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw Status.UNAUTHORIZED.get();
    }

    // step exists and belongs to task and project
    var step = stepJPA.findById(stepId).orElse(null);
    if (step == null || !step.getTask().getId().equals(taskId)
      || !step.getTask().getProject().getId().equals(projectId)) {
      throw Status.BAD_REQUEST.get();
    }

    return step;
  }


  public void delete(Long accountId, Long stepId, Long projectId, Long taskId) {
    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectIdAndProjectArchived(accountId, projectId, false)
      .orElseThrow(Status.NOT_FOUND);

    if (projectMeta.canNot(ProjectMemberPermission.MAN_TASKS)) {
      throw Status.UNAUTHORIZED.get();
    }

    // step exists and belongs to task and project
    var step = stepJPA.findById(stepId).orElse(null);
    if (step == null || !step.getTask().getId().equals(taskId)
      || !step.getTask().getProject().getId().equals(projectId)) {
      throw Status.BAD_REQUEST.get();
    }

    stepJPA.delete(step);
  }
}
