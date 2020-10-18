package io.github._2don.api.controllers;


import io.github._2don.api.models.Step;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.ProjectJPA;
import io.github._2don.api.repositories.StepJPA;
import io.github._2don.api.repositories.TaskJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/projects/tasks/steps")
public class StepController {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private StepJPA stepJPA;

  @GetMapping
  public List<Step> index() {
    return stepJPA.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Step store(@AuthenticationPrincipal Long accountId,
                    @RequestBody Step step) {
    var account = accountJPA.getOne(accountId);

    step.setCreatedBy(account);
    step.setUpdatedBy(account);

    return stepJPA.save(step);
  }

  @GetMapping("/{stepId}")
  public ResponseEntity<Step> show(@PathVariable("stepId") Long stepId) {
    return ResponseEntity.of(stepJPA.findById(stepId));
  }

  @PatchMapping("/{stepId}")
  public Step edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable("stepId") Long stepId,
                   @RequestBody Step step) {
    var stepEdit = stepJPA.findById(stepId)
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

  @DeleteMapping("/{stepId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("stepId") Long stepId) {

    stepJPA.delete(stepJPA.getOne(stepId));

  }
}
