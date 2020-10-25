package io.github._2don.api.controllers;

import io.github._2don.api.models.ProjectMembersPermissions;
import io.github._2don.api.models.Task;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.ProjectJPA;
import io.github._2don.api.repositories.ProjectMembersJPA;
import io.github._2don.api.repositories.TaskJPA;
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
@RequestMapping("/projects/tasks")
public class TaskController {

  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMembersJPA projectMembersJPA;

  @GetMapping("/projects/{projectsId}")
  public List<Task> index(@AuthenticationPrincipal Long accountId,
                          @PathVariable("projectsId") Long projectId) {

    if (!projectMembersJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return taskJPA.findAllByProjectId(projectId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  @PostMapping("/projects/{projectsId}")
  @ResponseStatus(HttpStatus.CREATED)
  public Task store(@AuthenticationPrincipal Long accountId,
                    @Validated @RequestBody Task task,
                    @PathVariable("projectsId") Long projectId) {

    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MODIFY) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var account = accountJPA.getOne(accountId);

    task.setCreatedBy(account);
    task.setUpdatedBy(account);

    return taskJPA.save(task);
  }

  @GetMapping("/projects/{projectsId}/task/{taskId}")
  public ResponseEntity<Task> show(@AuthenticationPrincipal Long accountId,
                                   @PathVariable("projectsId") Long projectId,
                                   @PathVariable("taskId") Long taskId) {

    if (!projectMembersJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    if (!taskJPA.existsById(taskId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.of(taskJPA.findById(taskId));
  }

  @PatchMapping("/projects/{projectsId}/task/{taskId}")
  public Task edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable("taskId") Long taskId,
                   @PathVariable("projectsId") Long projectId,
                   @Validated @RequestBody Task task) {

    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MANAGE) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

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

  @DeleteMapping("/projects/{projectsId}/task/{taskId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("taskId") Long taskId,
                      @PathVariable("projectsId") Long projectId) {
    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MODIFY) < 0){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    if (!taskJPA.existsById(taskId)){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    taskJPA.delete(taskJPA.getOne(taskId));
  }
}
