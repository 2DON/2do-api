package io.github._2don.api.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @GetMapping
  public List<Task> index(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId) {
    return taskService.getTasks(accountId, projectId);
  }

  // @PostMapping
  // @ResponseStatus(HttpStatus.CREATED)
  // public Task store(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId,
  // @Valid @RequestBody Task task) {
  //
  // return taskService.add(accountId, task, projectId);
  // }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Task store(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId,
                    @RequestParam String description, @RequestParam Long assignedToId) {

    // return taskService.add(accountId, task, projectId);
    return taskService.add(accountId, description, projectId, assignedToId);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<Task> show(@AuthenticationPrincipal Long accountId,
                                   @PathVariable Long projectId, @PathVariable Long taskId) {
    return ResponseEntity.ok(taskService.getTask(accountId, projectId, taskId));
  }

  @PatchMapping("/{taskId}")
  public Task edit(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId,
                   @PathVariable Long taskId, @RequestParam(name = "ordinal", required = false) Integer ordinal,
                   @RequestParam(name = "description", required = false) String description,
                   @RequestParam(name = "status", required = false) String status,
                   @RequestParam(name = "options", required = false) String options,
                   @RequestParam(name = "assignedToId", required = false) Long assignedToId) {
    // return taskService.update(accountId, projectId, taskId, task);

    return taskService.update(accountId, projectId, taskId, ordinal, description, status, options,
      assignedToId);
  }

  @DeleteMapping("/{taskId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long taskId,
                      @PathVariable Long projectId) {
    taskService.delete(accountId, taskId, projectId);
  }
}
