package io.github._2don.api.teammember;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams/{teamId}/members")
public class TeamMembersController {

  @Autowired
  private TeamMemberService teamMemberService;

  @GetMapping
  public List<TeamMember> index(@AuthenticationPrincipal Long accountId,
      @PathVariable Long teamId) {
    return teamMemberService.getTeamMembers(accountId, teamId);
  }

  @PostMapping("/{memberId}")
  @ResponseStatus(HttpStatus.CREATED)
  public TeamMember store(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId,
      @PathVariable Long memberId) {
    return teamMemberService.add(accountId, teamId, memberId);
  }

  @PatchMapping("/{memberId}")
  public TeamMember edit(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId,
      @PathVariable Long memberId, @RequestParam Boolean operator) {
    return teamMemberService.update(accountId, memberId, teamId, operator);
  }

  @DeleteMapping("/{accountId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountLoggedId, @PathVariable Long accountId,
      @PathVariable Long teamId) {
    teamMemberService.delete(accountLoggedId, accountId, teamId);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId) {
    teamMemberService.delete(accountId, teamId);
  }
}
