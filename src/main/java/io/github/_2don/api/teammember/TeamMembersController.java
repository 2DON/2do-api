package io.github._2don.api.teammember;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.team.TeamJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teams/{teamId}/members")
public class TeamMembersController {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TeamMembersJPA teamMembersJPA;
  @Autowired
  private TeamJPA teamJPA;

  @GetMapping
  public List<TeamMember> index(@AuthenticationPrincipal Long accountId,
                                @PathVariable Long teamId) {
    if (!teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return teamMembersJPA.findAllByTeamId(teamId);
  }

  // TODO(jonatanbirck): follow the patterns in use

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TeamMember store(@AuthenticationPrincipal Long accountId,
                          @PathVariable Long teamId,
                          @Valid @RequestBody TeamMember teamMember) {

    Account account = accountJPA.findById(accountId).orElse(null);
    if (account == null || !account.getPremium()){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var teamMembers = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    if(!teamMembers.getOperator()){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }


    // TODO(jonatanbirck): don't trust the received team member
    // TODO(jonatanbirck): check if account is in the team already
    // TODO(jonatanbirck): check if account has permission to add other account
    // TODO(jonatanbirck): receive the account to be added

     teamMember.setCreatedBy(account);
     teamMember.setUpdatedBy(account);

    return teamMembersJPA.save(teamMember);
  }

  // TODO(jonatanbirck): 'PATCH /'

  @DeleteMapping("/{accountId}/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountLoggedId, @PathVariable Long accountId,
                      Long teamId) {

    var teamMember = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    if (!teamMember.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(teamMember);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, Long teamId) {

    var teamMember = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // TODO(jonatanbirck): check if account is the only operator in the team, and if is return FORBIDDEN

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(teamMember);
  }
}
