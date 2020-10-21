package io.github._2don.api.controllers;

import io.github._2don.api.models.Task;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.ProjectJPA;
import io.github._2don.api.repositories.TaskJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/projects/tasks")
public class TaskController {

  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountJPA accountJPA;

  @GetMapping
  public List<Task> index() {
    return taskJPA.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Task store(@AuthenticationPrincipal Long accountId, @RequestBody Task task) {

    var account = accountJPA.getOne(accountId);

    task.setCreatedBy(account);
    task.setUpdatedBy(account);

    return taskJPA.save(task);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<Task> show(@PathVariable("taskId") Long taskId) {
    return ResponseEntity.of(taskJPA.findById(taskId));
  }

  @PatchMapping("/{taskId}")
  public Task edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable("taskId") Long taskId,
                   @Validated @RequestBody Task task) {

    var taskEdit = taskJPA.findById(taskId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (task.getOrdinal() != null) {
      taskEdit.setOrdinal(task.getOrdinal());
    }

    if (task.getDescription() != null) {
      if (task.getDescription().length() == 0) {
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

    taskEdit.setUpdatedBy(accountJPA.getOne(accountId));

    return taskJPA.save(taskEdit);
  }

  @DeleteMapping("/{taskId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("taskId") Long taskId) {

    taskJPA.delete(taskJPA.getOne(taskId));
  }
}
