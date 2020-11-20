package io.github._2don.api.team;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.teammember.TeamMember;
import io.github._2don.api.teammember.TeamMemberService;
import io.github._2don.api.utils.ImageEncoder;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

  @Autowired
  private TeamJPA teamJPA;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMemberService teamMemberService;
  @Autowired
  private AccountJPA accountJPA;

  public List<Team> getTeams(Long accountId) {
    return teamMemberService.getTeamMembers(accountId).stream().map(TeamMember::getTeam)
      .collect(Collectors.toList());
  }

  public Team getTeam(Long accountId, Long teamId) {
    if (!teamMemberService.exist(accountId, teamId)) {
      throw Status.UNAUTHORIZED.get();
    }

    return teamJPA.findById(teamId).orElseThrow(Status.UNAUTHORIZED);
  }

  public Team add(Long accountId, Team team) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);

    if (!account.getPremium()) {
      throw Status.UNAUTHORIZED.get();
    }

    team.setCreatedBy(account);
    team.setUpdatedBy(account);

    var teamCreated = teamJPA.save(team);

    teamMemberService.add(account.getId(), team.getId());

    return teamCreated;
  }

  public Team update(Long accountId,
                     Long teamId,
                     String name,
                     String removeAvatar,
                     MultipartFile avatar) {
    var teamMember = teamMemberService.getTeamMember(accountId, teamId);

    if (!teamMember.getOperator()) {
      throw Status.UNAUTHORIZED.get();
    }

    var team = teamJPA.findById(teamId).orElseThrow(Status.NOT_FOUND);

    if (name != null) {
      if (name.length() < 1 || name.length() > 45) {
        throw Status.BAD_REQUEST.get();
      }

      team.setName(name);
    }

    if (avatar != null) {
      if (!ImageEncoder.supports(avatar.getContentType())) {
        throw Status.BAD_REQUEST.get();
      }

      try {
        team.setAvatarUrl(ImageEncoder.encodeToString(avatar.getBytes()));
      } catch (IOException e) {
        throw Status.BAD_REQUEST.get();
      }

    } else if (removeAvatar != null) {
      team.setAvatarUrl(null);
    }

    team.setUpdatedBy(teamMember.getAccount());
    return teamJPA.save(team);
  }

  public void delete(Long accountId, Long teamId) {
    var teamMember = teamMemberService.getTeamMember(accountId, teamId);

    if (!teamMember.getOperator()) {
      throw Status.UNAUTHORIZED.get();
    }

    teamMemberService.delete(teamId);

    // TODO just delete?
    teamJPA.deleteById(teamId);
  }
}
