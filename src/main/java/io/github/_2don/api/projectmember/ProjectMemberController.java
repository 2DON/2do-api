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
                                @PathVariable Long projectId,
                                @RequestPart(value = "accountId") String accountId,
                                @RequestPart(value = "teamId", required = false) String teamId,
                                @RequestPart(value = "permission") String permission) {
    /*
     * Note:
     *  On the current version 2.0.4, Long and enum types don't work on @RequestPart,
     *  but is no mention on the documentation or at forums. And the error has, like
     *  most of spring boot errors, no explanation about what is the problem, just
     *  returns a ResponseStatusException of UNSUPPORTED_MEDIA_TYPE. If I remember
     *  is good to open a Issue about it, but i have no time for it now
     *
     * @author: wesauis
     * @date:   21-11-2020 (dd-mm-yyyy) 09:41 (GMT-3)
     */

    ProjectMemberPermission perm;
    try {
      perm = ProjectMemberPermission.valueOf(permission);
    } catch (IllegalArgumentException | NullPointerException ignored) {
      perm = null;
    }
    if (accountId == null || perm == null) {
      throw Status.BAD_REQUEST.get();
    }

    System.out.printf("{accountId: %s, teamId: %s, perm: '%s'}\n", accountId, teamId, perm.toString());
//    return projectMemberService.add(loggedId, projectId, accountId, teamId, permission);
    return null;
  }

  @PatchMapping
  public ProjectMemberDTO edit(@AuthenticationPrincipal Long loggedId,
                               @PathVariable Long projectId,
                               @RequestPart(name = "accountId") Long accountId,
                               @RequestPart(name = "teamId", required = false) Long teamId,
                               @RequestPart(name = "permissions") String permissions) {

    var projectMemberPermissions = ProjectMemberPermission.valueOf(permissions);

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
                      @RequestPart(name = "accountId") Long accountId) {
    projectMemberService.delete(loggedId, projectId, accountId);
  }
}
