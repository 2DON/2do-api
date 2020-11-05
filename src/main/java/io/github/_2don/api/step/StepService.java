package io.github._2don.api.step;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.task.TaskService;

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

  public Step edit(Long accountId, Long stepId, Long projectId, Long taskId, Step step) {

    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // step exists and belongs to task and project
    var stepEdit = stepJPA.findById(stepId).orElse(null);
    if (stepEdit == null || !stepEdit.getTask().getId().equals(taskId)
        || !stepEdit.getTask().getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (step.getDescription() != null) {
      if (step.getDescription().length() == 0 || step.getDescription().length() <= 80) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      stepEdit.setObservation(step.getObservation());
    }

    stepEdit.setUpdatedBy(accountService.getAccount(accountId));

    return stepJPA.save(stepEdit);
  }

  public Step add(Long accountId, Step step, Long projectId, Long taskId) {

    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // task exists and belongs to project
    var task = taskService.getTask(taskId, projectId);

    var account = accountService.getAccount(accountId);

    step.setCreatedBy(account).setUpdatedBy(account).setTask(task);

    return stepJPA.save(step);
  }

  public List<Step> getSteps(Long accountId, Long projectId, Long taskId) {
    // account is member of project
    if (!projectMemberService.exist(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // task exists and belongs to project
    if (!taskService.exist(projectId, taskId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return stepJPA.findAllByTaskId(taskId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  public Step getStep(Long accountId, Long stepId, Long projectId, Long taskId) {
    // account is member of project
    if (!projectMemberService.exist(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // step exists and belongs to task and project
    var step = stepJPA.findById(stepId).orElse(null);
    if (step == null || !step.getTask().getId().equals(taskId)
        || !step.getTask().getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return step;
  }


  public void delete(Long accountId, Long stepId, Long projectId, Long taskId) {
    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // step exists and belongs to task and project
    var step = stepJPA.findById(stepId).orElse(null);
    if (step == null || !step.getTask().getId().equals(taskId)
        || !step.getTask().getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    stepJPA.delete(step);
  }
}
