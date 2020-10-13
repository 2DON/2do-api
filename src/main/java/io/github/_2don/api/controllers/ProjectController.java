package io.github._2don.api.controllers;

import io.github._2don.api.models.Project;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.ProjectJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectJPA projectJPA;
  @Autowired
  private AccountJPA accountJPA;

  @GetMapping
  public List<Project> index() {
    return projectJPA.findAll();
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<Project> show(@PathVariable("id") Long id) {

    return ResponseEntity.of(projectJPA.findById(id));
  }

  @PatchMapping("/{projectId}")
  public Project edit(@AuthenticationPrincipal Long accountId, @PathVariable("id") Long projectId,
                      @RequestBody Project project) {

    var projectEdit = projectJPA.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


    if (project.getStatus() != null) {
      projectEdit.setStatus(project.getStatus());
    }

    if (project.getDescription() != null) {
      if (project.getDescription().length() == 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      projectEdit.setDescription(project.getDescription());
    }

    if (project.getArchived() != null) {
      projectEdit.setArchived(project.getArchived());
    }

    if (projectEdit.getObservation() != null) {
      if (projectEdit.getObservation().length() == 0) {
        project.setObservation(null);
      } else {
        project.setObservation(projectEdit.getObservation());
      }
    }

    if (projectEdit.getOptions() != null) {
      if (projectEdit.getOptions().length() == 0) {
        project.setOptions(null);
      } else {
        project.setOptions(projectEdit.getOptions());
      }
    }

    projectEdit.setUpdatedBy(accountJPA.getOne(accountId));

    return projectJPA.save(projectEdit);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Project store(@AuthenticationPrincipal Long accountId, @Validated @RequestBody Project project) {
    var account = accountJPA.getOne(accountId);

    project.setCreatedBy(account);
    project.setUpdatedBy(account);

    return projectJPA.save(project);
  }

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("projectId") Long projectId) {

    projectJPA.delete(projectJPA.getOne(projectId));

  }
}
