package io.github._2don.api.projectmember;

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
                                      @RequestParam Long projectId) {
    return projectMemberService.list(loggedId, projectId);
  }

  @PostMapping
  public ProjectMemberDTO store(@AuthenticationPrincipal Long loggedId,
                                @RequestParam Long projectId,
                                @RequestPart Long accountId,
                                @RequestPart(required = false) Long teamId,
                                @RequestPart ProjectMemberPermissions permissions) {
    return projectMemberService.add(loggedId, projectId, accountId, teamId, permissions);
  }

  @PatchMapping
  public ProjectMemberDTO edit(@AuthenticationPrincipal Long loggedId,
                               @RequestParam Long projectId,
                               @RequestPart Long accountId,
                               @RequestPart(required = false) Long teamId,
                               @RequestPart ProjectMemberPermissions permissions) {
    return projectMemberService.edit(loggedId, projectId, accountId, teamId, permissions);
  }

  @GetMapping("/{newOwnerId}")
  public void transferOwnership(@AuthenticationPrincipal Long loggedId,
                                @RequestParam Long projectId,
                                @PathVariable Long newOwnerId) {
    projectMemberService.transferOwnership(loggedId, projectId, newOwnerId);
  }

  @DeleteMapping
  public void destroy(@AuthenticationPrincipal Long loggedId,
                      @RequestParam Long projectId,
                      @RequestPart Long accountId) {
    projectMemberService.delete(loggedId, projectId, accountId);
  }
}
