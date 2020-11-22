package io.github._2don.api.projectmember;

import io.github._2don.api.utils.Convert;
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
    return projectMemberService.findMembers(loggedId, projectId);
  }

  @PostMapping
  public ProjectMemberDTO add(@AuthenticationPrincipal Long loggedId,
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

    var _accountId = Convert.toLong(accountId).orElseThrow(Status.BAD_REQUEST);
    var _permission = Convert.toProjectMemberPermission(permission).orElseThrow(Status.BAD_REQUEST);
    var _teamId = teamId == null ? null : Convert.toLong(teamId).orElseThrow(Status.BAD_REQUEST);

    return projectMemberService.addMember(loggedId, projectId, _accountId, _teamId, _permission);
  }

  @PatchMapping("/{accountId}")
  public ProjectMemberDTO update(@AuthenticationPrincipal Long loggedId,
                                 @PathVariable Long projectId,
                                 @PathVariable Long accountId,
                                 @RequestPart(value = "teamId", required = false) String teamId,
                                 @RequestPart(value = "permission", required = false) String permission) {
    var _permission = permission == null ? null : Convert.toProjectMemberPermission(permission).orElseThrow(Status.BAD_REQUEST);
    var _teamId = teamId == null ? null : Convert.toLong(teamId).orElseThrow(Status.BAD_REQUEST);

    return projectMemberService.update(loggedId, projectId, accountId, _teamId, _permission);
  }

  @DeleteMapping("/{accountId}")
  public void remove(@AuthenticationPrincipal Long loggedId,
                     @PathVariable Long projectId,
                     @PathVariable Long accountId) {
    projectMemberService.leaveOrRemoveMember(loggedId, projectId, accountId);
  }
}
