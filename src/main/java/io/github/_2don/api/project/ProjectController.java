package io.github._2don.api.project;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  @GetMapping
  public List<Project> index(@AuthenticationPrincipal Long accountId,
      @RequestParam(value = "archived", required = false,
          defaultValue = "false") Boolean archived) {
    return projectService.getAllProjectByAccountId(accountId, archived);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Project store(@AuthenticationPrincipal Long accountId,
      @Valid @RequestBody Project project) {
    return projectService.add(accountId, project);
  }

  @GetMapping("/{projectId}")
  public Project show(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId) {
    return projectService.getProject(accountId, projectId);
  }

  @PatchMapping("/{oldProjectId}")
  public Project edit(@AuthenticationPrincipal Long accountId, @PathVariable Long oldProjectId,
      @Valid @RequestBody Project newProject) {
    return projectService.update(accountId, oldProjectId, newProject);
  }

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long projectId) {
    projectService.delete(accountId, projectId);
  }
}
