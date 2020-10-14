package io.github._2don.api.controllers;

import io.github._2don.api.models.Project;
import io.github._2don.api.models.ProjectMembersPermissions;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.ProjectJPA;
import io.github._2don.api.repositories.ProjectMembersJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  @Autowired
  private ProjectMembersJPA projectMembersJPA;

  @GetMapping
  public List<Project> index() {
    // TODO should return a list of all the projects you are part of

    return projectJPA.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Project store(@AuthenticationPrincipal Long accountId,
                       @Validated @RequestBody Project project) {
    // TODO non premium can only be part of one team?

    var account = accountJPA.getOne(accountId);

    project.setCreatedBy(account);
    project.setUpdatedBy(account);

    return projectJPA.save(project);
  }

  @GetMapping("/{projectId}")
  public Project show(@AuthenticationPrincipal Long accountId,
                      @PathVariable("id") Long projectId) {
    if (!projectMembersJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return projectJPA.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  @PatchMapping("/{projectId}")
  public Project edit(@AuthenticationPrincipal Long accountId,
                      @PathVariable("id") Long projectId,
                      @RequestBody Project project) {
    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.MANAGE) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

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

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("projectId") Long projectId) {
    var projectMeta = projectMembersJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (projectMeta.getPermissions().compareTo(ProjectMembersPermissions.ALL) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO just delete?
    projectJPA.delete(projectJPA.getOne(projectId));
  }
}
