package io.github._2don.api.team;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.teammember.TeamMember;
import io.github._2don.api.teammember.TeamMemberService;
import io.github._2don.api.utils.ImageEncoder;

@Service
public class TeamService {

  @Autowired
  private TeamJPA teamJPA;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMemberService teamMemberService;

  public List<Team> getTeams(Long accountId) {
    return teamMemberService.getTeamMembers(accountId).stream().map(TeamMember::getTeam)
        .collect(Collectors.toList());
  }

  public Team getTeam(Long accountId, Long teamId) {

    if (!teamMemberService.exist(accountId, teamId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return teamJPA.findById(teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  public Team getTeam(Long teamId) {

    return teamJPA.findById(teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public Team add(Long accountId, Team team) {

    Account account = accountService.getAccount(accountId);

    if (!account.getPremium()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    team.setCreatedBy(account);
    team.setUpdatedBy(account);

    var teamCreated = teamJPA.save(team);

    teamMemberService.add(account.getId(), team.getId());

    return teamCreated;
  }

  public Team update(Long accountId, Long teamId, String name, String removeAvatar,
      MultipartFile avatar) {

    var teamMember = teamMemberService.getTeamMember(accountId, teamId);

    if (!teamMember.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var team = teamJPA.findById(teamId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (name != null) {
      if (name.length() < 1 || name.length() > 45) {
        throw new ResponseStatusException((HttpStatus.BAD_REQUEST));
      }

      team.setName(name);
    }

    if (avatar != null) {
      if (!ImageEncoder.MIME_TYPES.contains(avatar.getContentType())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      try {
        team.setAvatarUrl(ImageEncoder.encodeToString(avatar.getBytes()));
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

    } else if (removeAvatar != null) {
      team.setAvatarUrl(null);
    }

    team.setUpdatedBy(accountService.getAccount(accountId));
    return teamJPA.save(team);
  }

  public void delete(Long accountId, Long teamId) {

    var teamMember = teamMemberService.getTeamMember(accountId, teamId);

    if (!teamMember.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO just delete?
    teamJPA.delete(teamJPA.getOne(teamId));
  }
}
