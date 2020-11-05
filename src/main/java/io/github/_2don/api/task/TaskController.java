package io.github._2don.api.task;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @GetMapping
  public List<Task> index(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId) {
    return taskService.getTasks(accountId, projectId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Task store(@AuthenticationPrincipal Long accountId, @Validated @RequestBody Task task,
      @PathVariable Long projectId) {
    return taskService.add(accountId, task, projectId);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<Task> show(@AuthenticationPrincipal Long accountId,
      @PathVariable Long projectId, @PathVariable Long taskId) {
    return ResponseEntity.ok(taskService.getTask(accountId, projectId, taskId));
  }

  @PatchMapping("/{taskId}")
  public Task edit(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId,
      @PathVariable Long taskId, @RequestBody Task task) {
    return taskService.update(accountId, projectId, taskId, task);
  }

  @DeleteMapping("/{taskId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long taskId,
      @PathVariable Long projectId) {
    taskService.delete(accountId, taskId, projectId);
  }
}
