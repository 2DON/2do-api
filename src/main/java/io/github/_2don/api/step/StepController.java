package io.github._2don.api.step;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.project.ProjectJPA;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import io.github._2don.api.task.TaskJPA;

@RestController
@RequestMapping("/projects/{projectId}/tasks/{taskId}/steps")
public class StepController {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private StepJPA stepJPA;
  @Autowired
  private StepService stepService;

  @GetMapping
  public List<Step> index(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId,
      @PathVariable Long taskId) {

    // account is member of project
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // task exists and belongs to project
    if (!taskJPA.existsByIdAndProjectId(taskId, projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return stepJPA.findAllByTaskId(taskId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Step store(@AuthenticationPrincipal Long accountId, @Validated @RequestBody Step step,
      @PathVariable Long projectId, @PathVariable Long taskId) {

    // account is member of project and has permission
    var projectMeta =
        projectMemberJPA.findByAccountIdAndProjectId(accountId, projectId).orElse(null);
    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // task exists and belongs to project
    var task = taskJPA.findByIdAndProjectId(taskId, projectId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    var account = accountJPA.getOne(accountId);

    step.setCreatedBy(account).setUpdatedBy(account).setTask(task);

    return stepJPA.save(step);
  }

  @GetMapping("/{stepId}")
  public Step show(@AuthenticationPrincipal Long accountId, @PathVariable Long stepId,
      @PathVariable Long projectId, @PathVariable Long taskId) {

    // account is member of project
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
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

  @PatchMapping("/{stepId}")
  public Step edit(@AuthenticationPrincipal Long accountId, @PathVariable Long stepId,
      @PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Step step) {

    // account is member of project and has permission
    var projectMeta =
        projectMemberJPA.findByAccountIdAndProjectId(accountId, projectId).orElse(null);
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

    stepEdit.setUpdatedBy(accountJPA.getOne(accountId));

    return stepJPA.save(stepEdit);
  }

  @DeleteMapping("/{stepId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long stepId,
      @PathVariable Long projectId, @PathVariable Long taskId) {

    stepService.delete(accountId, stepId, projectId, taskId);
  }
}
