package io.github._2don.api.teammember;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.team.TeamJPA;
import io.github._2don.api.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamMemberService {

  @Autowired
  private TeamService teamService;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMembersJPA teamMembersJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TeamJPA teamJPA;

  public TeamMember add(Long accountId, Long teamId) {

    var account = accountService.getAccount(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    var team = teamJPA.findById(teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!account.getPremium()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    TeamMember teamMember = new TeamMember();
    teamMember.setCreatedBy(account);
    teamMember.setOperator(true);
    teamMember.setAccount(account);
    teamMember.setUpdatedBy(account);
    teamMember.setTeam(team);

    return teamMembersJPA.save(teamMember);
  }

  public TeamMember add(Long accountId, Long teamId, Long memberId) {


    var account = accountService.getAccount(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    var member = accountService.getAccount(memberId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    var team = teamJPA.findById(teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!account.getPremium()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var teamMembers = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    if (!teamMembers.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    TeamMember teamMember = new TeamMember();
    teamMember.setCreatedBy(account);
    teamMember.setOperator(false);
    teamMember.setAccount(member);
    teamMember.setUpdatedBy(account);
    teamMember.setTeam(team);

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
    if (!operator && teamMembersJPA.countByTeamIdAndOperator(teamId, true) < 2) {
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }

    accountMeta.setOperator(operator).setUpdatedBy(loggedMeta.getAccount());
    return teamMembersJPA.save(accountMeta);
  }

  public void delete(Long accountLoggedId, Long accountId, Long teamId) {

    var loggedMeta = getTeamMember(accountLoggedId, teamId);
    var accountMeta = getTeamMember(accountId, teamId);

    if (!loggedMeta.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    Long countOpertors = teamMembersJPA.countByTeamIdAndOperator(teamId, true);

    if (countOpertors <= 1) {
      new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(accountMeta);
  }

  public void delete(Long accountId, Long teamId) {
    var teamMember = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    Long countOpertors = teamMembersJPA.countByTeamIdAndOperator(teamId, true);

    if (countOpertors <= 1) {
      new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(teamMember);
  }

  public void delete(Long teamId) {
    List<TeamMember> teamsMember = teamMembersJPA.findAllByTeamId(teamId);

    for (TeamMember teamMember : teamsMember) {
      teamMembersJPA.delete(teamMember);
    }
  }

}
