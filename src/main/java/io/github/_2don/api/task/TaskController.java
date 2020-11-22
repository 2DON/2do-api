package io.github._2don.api.task;

import io.github._2don.api.utils.Convert;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @GetMapping
  public List<TaskDTO> index(@AuthenticationPrincipal Long accountId,
                             @PathVariable Long projectId) {
    return taskService.findTasks(accountId, projectId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskDTO store(@AuthenticationPrincipal Long accountId,
                       @PathVariable Long projectId,
                       @RequestPart(name = "description") String description,
                       @RequestPart(name = "ordinal", required = false) Integer ordinal) {
    return taskService.create(accountId, projectId, description, ordinal);
  }

  @PatchMapping("/{taskId}")
  public TaskDTO update(@AuthenticationPrincipal Long accountId,
                        @PathVariable Long projectId,
                        @PathVariable Long taskId,
                        @RequestParam(name = "description", required = false) String description,
                        @RequestParam(name = "ordinal", required = false) Integer ordinal,
                        @RequestParam(name = "status", required = false) String status,
                        @RequestParam(name = "options", required = false) String options,
                        @RequestParam(name = "assignedTo", required = false) String assignedTo) {
    var _status = status == null
      ? null
      : Convert.toEnum(TaskStatus.class, status).orElseThrow(Status.BAD_REQUEST);

    var _assignedTo = assignedTo == null
      ? null
      : Convert.toLong(assignedTo).orElseThrow(Status.BAD_REQUEST);

    return taskService.update(accountId, projectId, taskId, description, ordinal, _status, options, _assignedTo);
  }

  @DeleteMapping("/{taskId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long projectId,
                      @PathVariable Long taskId) {
    taskService.delete(accountId, projectId, taskId);
  }
}
