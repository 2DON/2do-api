package io.github._2don.api.teammember;

import io.github._2don.api.utils.Convert;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams/{teamId}/members")
public class TeamMemberController {

  @Autowired
  private TeamMemberService teamMemberService;

  @GetMapping
  public List<TeamMemberDTO> index(@AuthenticationPrincipal Long accountId,
                                   @PathVariable Long teamId) {
    System.out.println("AQ CHEGOU");
    return teamMemberService.findMembers(accountId, teamId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TeamMemberDTO store(@AuthenticationPrincipal Long loggedId,
                             @PathVariable Long teamId,
                             @RequestPart(value = "accountId") String accountId) {
    var _accountId
      = Convert.toLong(accountId)
      .orElseThrow(Status.BAD_REQUEST);

    return teamMemberService.addMember(loggedId, teamId, _accountId);
  }

  @PatchMapping("/{accountId}")
  public TeamMemberDTO update(@AuthenticationPrincipal Long loggedId,
                              @PathVariable Long teamId,
                              @PathVariable Long accountId,
                              @RequestPart(name = "operator") String operator) {
    var _operator = Convert.toBoolean(operator).orElseThrow(Status.BAD_REQUEST);

    return teamMemberService.update(loggedId, teamId, accountId, _operator);
  }

  @DeleteMapping("/{accountId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long loggedId,
                      @PathVariable Long teamId,
                      @PathVariable Long accountId) {
    teamMemberService.removeMember(loggedId, teamId, accountId);
  }
}
