package io.github._2don.api.projectmember;

import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
      throw Status.BAD_REQUEST.get();
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
      throw Status.BAD_REQUEST.get();
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
