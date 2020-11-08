package io.github._2don.api.projectmember;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

  @Autowired
  private ProjectMemberService projectMemberService;

  @GetMapping
  public List<ProjectMemberDTO> index(@AuthenticationPrincipal Long loggedId,
      @PathVariable Long projectId) {
    return projectMemberService.list(loggedId, projectId);
  }

  @PostMapping
  public ProjectMemberDTO store(@AuthenticationPrincipal Long loggedId,
      @PathVariable Long projectId, @RequestParam(name = "accountId") Long accountId,
      @RequestParam(name = "teamId", required = false) Long teamId,
      @RequestParam(name = "permissions") String permissions) {

    var projectMemberPermissions = ProjectMemberPermissions.valueOf(permissions);

    if (projectMemberPermissions == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return projectMemberService.add(loggedId, projectId, accountId, teamId,
        projectMemberPermissions);
  }

  @PatchMapping
  public ProjectMemberDTO edit(@AuthenticationPrincipal Long loggedId, @PathVariable Long projectId,
      @RequestParam(name = "accountId") Long accountId,
      @RequestParam(name = "teamId", required = false) Long teamId,
      @RequestParam(name = "permissions") String permissions) {

    var projectMemberPermissions = ProjectMemberPermissions.valueOf(permissions);

    if (projectMemberPermissions == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return projectMemberService.edit(loggedId, projectId, accountId, teamId,
        projectMemberPermissions);
  }

  @GetMapping("/{newOwnerId}")
  public void transferOwnership(@AuthenticationPrincipal Long loggedId,
      @PathVariable Long projectId, @PathVariable Long newOwnerId) {
    projectMemberService.transferOwnership(loggedId, projectId, newOwnerId);
  }

  @DeleteMapping
  public void destroy(@AuthenticationPrincipal Long loggedId, @PathVariable Long projectId,
      @RequestParam(name = "accountId") Long accountId) {
    projectMemberService.delete(loggedId, projectId, accountId);
  }
}
