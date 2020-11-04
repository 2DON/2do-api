package io.github._2don.api.step;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.projectmember.ProjectMemberPermissions;
import io.github._2don.api.projectmember.ProjectMemberService;

@Service
public class StepService {

  @Autowired
  private AccountService accountService;
  @Autowired
  private StepJPA stepJPA;
  @Autowired
  private ProjectMemberService projectMemberService;

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
