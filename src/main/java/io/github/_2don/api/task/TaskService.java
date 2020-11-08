package io.github._2don.api.task;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import io.github._2don.api.projectmember.ProjectMemberService;

@Service
public class TaskService {

  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private AccountService accountService;
  @Autowired
  private ProjectMemberService projectMemberService;

  public Task add(Long accountId, Task task, Long projectId) {
    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var account = accountService.getAccount(accountId);

    task.setCreatedBy(account).setUpdatedBy(account).setProject(projectMeta.getProject());

    return taskJPA.save(task);
  }

  public Task add(Long accountId, String description, Long projectId, Long assignedToId) {
    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var account = accountService.getAccount(accountId);
    var assignedTo = accountService.getAccount(assignedToId);

    Task task = new Task();
    task.setCreatedBy(account);
    task.setUpdatedBy(account);
    task.setProject(projectMeta.getProject());
    task.setDescription(description);
    task.setAssignedTo(assignedTo);

    return taskJPA.save(task);
  }

  public Task update(Long accountId, Long projectId, Long taskId, Task task) {
    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var taskEdit = taskJPA.findByIdAndProjectId(taskId, projectId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (task.getOrdinal() != null) {
      taskEdit.setOrdinal(task.getOrdinal());
    }

    if (task.getDescription() != null) {
      if (task.getDescription().length() == 0 || task.getDescription().length() >= 80) {
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

    taskEdit.setUpdatedBy(accountService.getAccount(accountId));

    return taskJPA.save(taskEdit);
  }

  public Task update(Long accountId, Long projectId, Long taskId, Integer ordinal,
      String description, String status, String options, Long assignedToId) {

    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var taskEdit = taskJPA.findByIdAndProjectId(taskId, projectId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (ordinal != null) {
      taskEdit.setOrdinal(ordinal);
    }

    if (description != null) {
      if (description.length() == 0 || description.length() >= 80) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      taskEdit.setDescription(description);
    }

    if (status != null) {

      TaskStatus taskStatus = TaskStatus.valueOf(status);

      if (taskStatus != null) {
        taskEdit.setStatus(taskStatus);
      }
    }

    if (options != null) {
      if (options.length() == 0) {
        taskEdit.setOptions(null);
      }
      taskEdit.setOptions(options);
    }

    if (assignedToId != null) {
      Account assignedTo = accountService.getAccount(assignedToId);

      taskEdit.setAssignedTo(assignedTo);
    }

    taskEdit.setUpdatedBy(accountService.getAccount(accountId));

    return taskJPA.save(taskEdit);
  }

  public void delete(Long accountId, Long taskId, Long projectId) {
    // account is member of project and has permission
    var projectMeta = projectMemberService.getProjectMember(accountId, projectId).orElse(null);

    if (projectMeta == null
        || projectMeta.getPermissions().compareTo(ProjectMemberPermissions.MAN_TASKS) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var task = taskJPA.findById(taskId).orElse(null);
    if (task == null || !task.getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    taskJPA.delete(task);
  }

  public Task getTask(Long accountId, Long projectId, Long taskId) {

    // account is member of project
    if (!projectMemberService.exist(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // task exists and belongs to project
    var task = taskJPA.findById(taskId).orElse(null);
    if (task == null || !task.getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return taskJPA.getOne(taskId);
  }

  public List<Task> getTasks(Long accountId, Long projectId) {

    // account is member of project
    if (!projectMemberService.exist(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return taskJPA.findAllByProjectId(projectId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  public Task getTask(Long projectId, Long taskId) {

    // task exists and belongs to project
    var task = taskJPA.findById(taskId).orElse(null);
    if (task == null || !task.getProject().getId().equals(projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return taskJPA.getOne(taskId);
  }

  public boolean exist(Long projectId, Long taskId) {
    return taskJPA.existsByIdAndProjectId(taskId, projectId);
  }

}
