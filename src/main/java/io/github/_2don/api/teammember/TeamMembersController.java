package io.github._2don.api.teammember;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.team.TeamJPA;

@RestController
@RequestMapping("/teams/members")
public class TeamMembersController {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TeamMembersJPA teamMembersJPA;
  @Autowired
  private TeamJPA teamJPA;

  @GetMapping
  public List<TeamMember> index(@AuthenticationPrincipal Long accountId) {

    return teamMembersJPA.findAllByAccountId(accountId);
  }

  @GetMapping("/{teamId}")
  public List<TeamMember> index(@AuthenticationPrincipal Long accountId,
      @PathVariable Long teamId) {

    return teamMembersJPA.findAllByTeamId(teamId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TeamMember store(@AuthenticationPrincipal Long accountId,
      @Valid @RequestBody TeamMember teamMember) {

    var account = accountJPA.findById(accountId).orElse(null);

    if (account == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    teamMember.setOperator(true);

    return teamMembersJPA.save(teamMember);
  }

  @DeleteMapping("/{accountId}/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountLoggedId, @PathVariable Long accountId,
      Long teamId) {

    var teamMember = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    if (!teamMember.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO just delete?
    teamMembersJPA.delete(teamMember);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, Long teamId) {

    var teamMember = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // TODO just delete?
    teamMembersJPA.delete(teamMember);
  }
}
