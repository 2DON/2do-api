package io.github._2don.api.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectService projectService;

  @GetMapping
  public List<ProjectDTO> index(@AuthenticationPrincipal Long accountId,
                                @RequestParam(value = "achieved", required = false, defaultValue = "false") Boolean achieved) {
    return projectService.findProjects(accountId, achieved);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProjectDTO store(@AuthenticationPrincipal Long accountId,
                          @RequestPart(name = "description") String description,
                          @RequestPart(name = "observation", required = false) String observation,
                          @RequestPart(name = "ordinal", required = false) Integer ordinal) {
    return projectService.create(accountId, description, observation, ordinal);
  }

  @GetMapping("/{projectId}")
  public ProjectDTO show(@AuthenticationPrincipal Long accountId,
                         @PathVariable Long projectId) {
    return projectService.find(accountId, projectId);
  }

  @PatchMapping("/{projectId}")
  public ProjectDTO update(@AuthenticationPrincipal Long accountId,
                           @PathVariable Long projectId,
                           @RequestPart(name = "description", required = false) String description,
                           @RequestPart(name = "observation", required = false) String observation,
                           @RequestPart(name = "ordinal", required = false) Integer ordinal,
                           @RequestPart(name = "options", required = false) String options) {
    return projectService.update(accountId, projectId, description, observation, ordinal, options);
  }

  @PutMapping("/{projectId}/icon")
  public ProjectDTO updateIcon(@AuthenticationPrincipal Long accountId,
                               @PathVariable Long projectId,
                               @RequestPart(name = "icon", required = false) MultipartFile icon) {
    return projectService.updateIcon(accountId, projectId, icon);
  }

  @GetMapping("/{projectId}/toggle-archiving")
  public ProjectDTO toggleArchiving(@AuthenticationPrincipal Long accountId,
                                    @PathVariable Long projectId) {
    return projectService.toggleArchiving(accountId, projectId);
  }

  @GetMapping("/{projectId}/transfer-to/{newOwnerId}")
  public void transferOwnership(@AuthenticationPrincipal Long loggedId,
                                @PathVariable Long projectId,
                                @PathVariable Long newOwnerId) {
    projectService.transferOwnership(loggedId, projectId, newOwnerId);
  }

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long projectId) {
    projectService.delete(accountId, projectId);
  }

}
