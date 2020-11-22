package io.github._2don.api.teammember;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.team.TeamJPA;
import io.github._2don.api.team.TeamService;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    var account = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);
    var team = teamJPA.findById(teamId).orElseThrow(Status.NOT_FOUND);

    if (!account.getPremium()) {
      throw Status.UNAUTHORIZED.get();
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
    var account = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);
    var member = accountJPA.findById(memberId).orElseThrow(Status.NOT_FOUND);
    var team = teamJPA.findById(teamId).orElseThrow(Status.NOT_FOUND);

    if (!account.getPremium()) {
      throw Status.UNAUTHORIZED.get();
    }

    var teamMembers = teamMembersJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(Status.UNAUTHORIZED);

    if (!teamMembers.getOperator()) {
      throw Status.UNAUTHORIZED.get();
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
      throw Status.UNAUTHORIZED.get();
    }
  }

  public List<TeamMember> getTeamMembers(Long accountId, Long teamId) {
    if (!teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw Status.UNAUTHORIZED.get();
    }

    return teamMembersJPA.findAllByTeamId(teamId);
  }

  public TeamMember getTeamMember(Long accountId, Long teamId) {
    return teamMembersJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(Status.UNAUTHORIZED);
  }

  public boolean exist(Long accountId, Long teamId) {
    return teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId);
  }

  public TeamMember update(Long accountId, Long memberId, Long teamId, Boolean operator) {
    var loggedMeta = getTeamMember(accountId, teamId);
    var accountMeta = getTeamMember(memberId, teamId);

    if (!loggedMeta.getOperator()) {
      throw Status.UNAUTHORIZED.get();
    }
    if (!operator && teamMembersJPA.countByTeamIdAndOperator(teamId, true) < 2) {
      throw Status.LOCKED.get();
    }

    accountMeta.setOperator(operator).setUpdatedBy(loggedMeta.getAccount());
    return teamMembersJPA.save(accountMeta);
  }

  public void delete(Long accountLoggedId, Long accountId, Long teamId) {
    var loggedMeta = getTeamMember(accountLoggedId, teamId);
    var accountMeta = getTeamMember(accountId, teamId);

    if (!loggedMeta.getOperator()) {
      throw Status.UNAUTHORIZED.get();
    }

    Long countOpertors = teamMembersJPA.countByTeamIdAndOperator(teamId, true);

    if (countOpertors <= 1) {
      throw Status.UNAUTHORIZED.get();
    }

    // TODO(jonatanbirck): just delete?
    teamMembersJPA.delete(accountMeta);
  }

  public void delete(Long accountId, Long teamId) {
    var teamMember = teamMembersJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(Status.UNAUTHORIZED);

    Long countOpertors = teamMembersJPA.countByTeamIdAndOperator(teamId, true);

    if (countOpertors <= 1) {
      throw Status.UNAUTHORIZED.get();
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
