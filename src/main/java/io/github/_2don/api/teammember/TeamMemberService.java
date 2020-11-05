package io.github._2don.api.teammember;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.team.Team;
import io.github._2don.api.team.TeamService;

@Service
public class TeamMemberService {

  @Autowired
  private TeamService teamService;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMembersJPA teamMembersJPA;

  public TeamMember add(Long accountId, Long teamId) {

    Account account = accountService.getAccount(accountId);
    Team team = teamService.getTeam(teamId);

    if (!account.getPremium()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    TeamMember teamMember = new TeamMember();
    teamMember.setCreatedBy(account);
    teamMember.setOperator(true);
    teamMember.setAccount(account);
    teamMember.setUpdatedBy(account);
    teamMember.setTeam(team);

    // TODO(jonatanbirck): don't trust the received team member
    // TODO(jonatanbirck): check if account is in the team already
    // TODO(jonatanbirck): check if account has permission to add other account
    // TODO(jonatanbirck): receive the account to be added

    return teamMembersJPA.save(teamMember);
  }

  public TeamMember add(Long accountId, Long teamId, TeamMember teamMember) {

    Account account = accountService.getAccount(accountId);

    if (!account.getPremium()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var teamMembers = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    if (!teamMembers.getOperator()) {
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

  public void assertIsMember(Long accountId, Long teamId) {
    if (!teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  public List<TeamMember> getTeamMembers(Long accountId, Long teamId) {

    if (!teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return teamMembersJPA.findAllByTeamId(teamId);
  }

  public List<TeamMember> getTeamMembers(Long accountId) {
    return teamMembersJPA.findAllByAccountId(accountId);
  }

  public TeamMember getTeamMember(Long accountId, Long teamId) {
    return teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  public boolean exist(Long accountId, Long teamId) {
    return teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId);
  }

  public TeamMember update(Long accountId, Long memberId, Long teamId, Boolean operator) {

    var loggedMeta = getTeamMember(accountId, teamId);
    var accountMeta = getTeamMember(memberId, teamId);

    if (!loggedMeta.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (teamMembersJPA.countByTeamIdAndOperator(teamId, true) < 2) {
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }

    accountMeta.setOperator(operator).setUpdatedBy(loggedMeta.getAccount());
    return teamMembersJPA.save(accountMeta);
  }

  public void delete(Long accountLoggedId, Long accountId, Long teamId) {

    var loggedMeta = getTeamMember(accountLoggedId, teamId);
    var accountMeta = getTeamMember(accountId, teamId);

    if (!loggedMeta.getOperator() || accountLoggedId.equals(accountId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(accountMeta);
  }

  public void delete(Long accountId, Long teamId) {
    var teamMember = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // TODO(jonatanbirck): check if account is the only operator in the team, and if is return
    // FORBIDDEN

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(teamMember);
  }

}
