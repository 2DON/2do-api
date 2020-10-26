package io.github._2don.api.task;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.project.ProjectJPA;
import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;

  @GetMapping
  public List<Task> index(@AuthenticationPrincipal Long accountId,
                          @PathVariable Long projectId) {

    // account is member of project
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return taskJPA.findAllByProjectId(projectId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Task store(@AuthenticationPrincipal Long accountId,
                    @Validated @RequestBody Task task,
                    @PathVariable Long projectId) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElse(null);
    if (projectMeta == null
      || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var account = accountJPA.getOne(accountId);

    task
      .setCreatedBy(account)
      .setUpdatedBy(account)
      .setProject(projectMeta.getProject());

    return taskJPA.save(task);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<Task> show(@AuthenticationPrincipal Long accountId,
                                   @PathVariable Long projectId,
                                   @PathVariable Long taskId) {

    // account is member of project
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // task exists and belongs to project
    var task = taskJPA.findById(taskId).orElse(null);
    if (task == null
      || !task.getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.of(taskJPA.findById(taskId));
  }

  @PatchMapping("/{taskId}")
  public Task edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable Long projectId,
                   @PathVariable Long taskId,
                   @RequestBody Task task) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElse(null);
    if (projectMeta == null
      || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var taskEdit = taskJPA
      .findByIdAndProjectId(taskId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (task.getOrdinal() != null) {
      taskEdit.setOrdinal(task.getOrdinal());
    }

    if (task.getDescription() != null) {
      if (task.getDescription().length() == 0
        || task.getDescription().length() <= 80) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      taskEdit.setDescription(task.getDescription());
    }

    if (task.getStatus() != null) {
      taskEdit.setStatus(task.getStatus());
    }

    if (task.getOptions() != null) {
      if (task.getOptions().length() == 0) {
        taskEdit.setOptions(null);
      }
      taskEdit.setOptions(task.getOptions());
    }

    if (task.getAssignedTo() != null) {
      taskEdit.setAssignedTo(task.getAssignedTo());
    }

    taskEdit.setUpdatedBy(accountJPA.getOne(accountId));

    return taskJPA.save(taskEdit);
  }

  @DeleteMapping("/{taskId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long taskId,
                      @PathVariable Long projectId) {

    // account is member of project and has permission
    var projectMeta = projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElse(null);
    if (projectMeta == null
      || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var task = taskJPA.findById(taskId).orElse(null);
    if (task == null
      || !task.getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    taskJPA.delete(task);
  }
}
