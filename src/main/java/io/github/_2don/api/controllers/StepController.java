package io.github._2don.api.controllers;


import io.github._2don.api.models.ProjectMembersPermissions;
import io.github._2don.api.models.Step;
import io.github._2don.api.repositories.*;
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
public class StepController {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private ProjectMembersJPA projectMembersJPA;
  @Autowired
  private StepJPA stepJPA;

  @GetMapping("/projects/{projectId}/tasks/{taskId}")
  public List<Step> index(@AuthenticationPrincipal Long accountId,
                          @PathVariable("projectId") Long projectId,
                          @PathVariable("taskId") Long taskId) {
    if(!projectMembersJPA.existsByAccountIdAndProjectId(accountId, projectId)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (!taskJPA.existsByTaskIdAndProjectId(taskId, projectId)){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return stepJPA.findAllByTaskId(taskId, Sort.by(Sort.Direction.ASC, "ordinal"));
  }

  @PostMapping("/projects/{projectId}/tasks/{taskId}")
  @ResponseStatus(HttpStatus.CREATED)
  public Step store(@AuthenticationPrincipal Long accountId,
                    @Validated @RequestBody Step step,
                    @PathVariable("projectId") Long projectId,
                    @PathVariable("taskId") Long taskId) {
    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MODIFY) < 0){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    if (!taskJPA.existsByTaskIdAndProjectId(taskId, projectId)){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    var account = accountJPA.getOne(accountId);

    step.setCreatedBy(account);
    step.setUpdatedBy(account);

    return stepJPA.save(step);
  }

  @GetMapping("/projects/{projectId}/tasks/{taskId}/steps/{stepsId}")
  public ResponseEntity<Step> show(@AuthenticationPrincipal Long accountId,
                                   @PathVariable("stepsId") Long stepsId,
                                   @PathVariable("projectId") Long projectId,
                                   @PathVariable("taskId") Long taskId) {

    if (!projectMembersJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    if (!taskJPA.existsByTaskIdAndProjectId(taskId, projectId)){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.of(stepJPA.findById(stepsId));
  }

  @PatchMapping("/projects/{projectId}/tasks/{taskId}/steps/{stepsId}")
  public Step edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable("stepsId") Long stepsId,
                   @PathVariable("projectId") Long projectId,
                   @PathVariable("taskId") Long taskId,
                   @RequestBody Step step) {

    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MANAGE) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (!taskJPA.existsByTaskIdAndProjectId(taskId, projectId)){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    var stepEdit = stepJPA.findById(stepsId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (step.getDescription() != null) {
      if (step.getDescription().length() == 0) {
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
      stepEdit.setObservation(step.getObservation());
    }

    stepEdit.setUpdatedBy(accountJPA.getOne(accountId));

    return stepJPA.save(stepEdit);
  }

  @DeleteMapping("/projects/{projectId}/tasks/{taskId}/steps/{stepsId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("stepId") Long stepId,
                      @PathVariable("projectId") Long projectId,
                      @PathVariable("taskId") Long taskId
                      ) {

    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MODIFY) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    if (!taskJPA.existsByTaskIdAndProjectId(taskId, projectId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    stepJPA.delete(stepJPA.getOne(stepId));

  }
}
