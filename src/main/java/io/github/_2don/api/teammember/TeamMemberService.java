package io.github._2don.api.teammember;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.team.TeamJPA;
import io.github._2don.api.team.TeamService;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamMemberService {

  @Autowired
  private TeamService teamService;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMemberJPA teamMemberJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TeamJPA teamJPA;

  public void assertIsMember(Long accountId, Long teamId) {
    if (!teamMemberJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw Status.UNAUTHORIZED.get();
    }
  }

  public List<TeamMemberDTO> findMembers(Long accountId, Long teamId) {
    System.out.println(teamId);
    System.out.println(teamMemberJPA
      .findAllByTeamId(teamId));

    assertIsMember(accountId, teamId);

    return teamMemberJPA
      .findAllByTeamId(teamId)
      .stream()
      .map(TeamMemberDTO::new)
      .collect(Collectors.toList());
  }

  public TeamMemberDTO addMember(Long loggedId, Long teamId, Long accountId) {
    var logged = teamMemberJPA
      .findByAccountIdAndTeamIdAndOperator(loggedId, teamId, true)
      .orElseThrow(Status.UNAUTHORIZED);

    if (teamMemberJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw Status.CONFLICT.get();
    }

    var target = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);

    if (!target.getPremium()) {
      throw Status.UPGRADE_REQUIRED.get();
    }

    var member = new TeamMember()
      .setAccount(target)
      .setTeam(teamJPA.getOne(teamId))
      .setOperator(false)
      .setCreatedBy(logged.getAccount())
      .setUpdatedBy(logged.getAccount());

    return new TeamMemberDTO(teamMemberJPA.save(member));
  }

  public TeamMemberDTO update(Long loggedId, Long teamId, Long accountId, Boolean operator) {
    var logged = teamMemberJPA
      .findByAccountIdAndTeamIdAndOperator(loggedId, teamId, true)
      .orElseThrow(Status.UNAUTHORIZED);

    var target = teamMemberJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(Status.NOT_FOUND);

    if (!operator && target.getOperator()) {
      var operators = teamMemberJPA.countByTeamIdAndOperator(teamId, true);

      if (target.getOperator() && operators <= 1) {
        throw Status.LOCKED.get();
      }
    }

    target
      .setOperator(operator)
      .setUpdatedBy(logged.getAccount());

    return new TeamMemberDTO(teamMemberJPA.save(target));
  }

  public void removeMember(Long loggedId, Long teamId, Long accountId) {
    var target = teamMemberJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(Status.NOT_FOUND);

    if (accountId.equals(loggedId)) {
      var operators = teamMemberJPA.countByTeamIdAndOperator(teamId, true);

      if (target.getOperator() && operators <= 1) {
        throw Status.LOCKED.get();
      }
    } else {
      if (!teamMemberJPA.existsByAccountIdAndTeamIdAndOperator(loggedId, teamId, true)) {
        throw Status.UNAUTHORIZED.get();
      }
    }

    teamMemberJPA.delete(target);
  }

}
