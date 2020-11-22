package io.github._2don.api.team;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.teammember.TeamMember;
import io.github._2don.api.teammember.TeamMembersJPA;
import io.github._2don.api.utils.ImageEncoder;
import io.github._2don.api.utils.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

  @Autowired
  private TeamJPA teamJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TeamMembersJPA teamMembersJPA;

  public List<TeamDTO> findTeams(@NonNull Long accountId) {
    if (!accountJPA.findById(accountId).map(Account::getPremium).orElseThrow(Status.NOT_FOUND)) {
      throw Status.UPGRADE_REQUIRED.get();
    }

    return teamMembersJPA
      .findAllByAccountId(accountId)
      .stream()
      .map(TeamDTO::new)
      .collect(Collectors.toList());
  }

  public Optional<TeamDTO> find(@NonNull Long accountId,
                                @NonNull Long teamId) {
    return teamMembersJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .map(TeamDTO::new);
  }

  public TeamDTO create(@NonNull Long accountId,
                        @NonNull String name) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.GONE);
    if (!account.getPremium()) {
      throw Status.UPGRADE_REQUIRED.get();
    }

    if (name.length() < 1 || name.length() >= 45) {
      throw Status.BAD_REQUEST.get();
    }

    var team = new Team()
      .setName(name)
      .setCreatedBy(account)
      .setUpdatedBy(account);

    team = teamJPA.save(team);

    var member = teamMembersJPA.save(new TeamMember()
      .setAccount(account)
      .setTeam(team)
      .setOperator(true)
      .setCreatedBy(account)
      .setUpdatedBy(account));

    return new TeamDTO(member);
  }

  public TeamDTO update(@NotNull Long accountId,
                        @NotNull Long teamId,
                        @NotNull String name) {
    var member = teamMembersJPA.findByAccountIdAndTeamIdAndOperator(accountId, teamId, true).orElseThrow(Status.UNAUTHORIZED);

    var team = member.getTeam();

    if (name.length() < 1 || name.length() >= 45) {
      throw Status.BAD_REQUEST.get();
    }

    team
      .setName(name)
      .setUpdatedBy(member.getAccount());

    team = teamJPA.save(team);

    return new TeamDTO(team, true);
  }

  public TeamDTO updateIcon(@NonNull Long accountId,
                            @NonNull Long teamId,
                            MultipartFile icon) {
    var member = teamMembersJPA.findByAccountIdAndTeamIdAndOperator(accountId, teamId, true).orElseThrow(Status.UNAUTHORIZED);

    var team = member.getTeam();

    if (icon == null || icon.isEmpty()) {
      team
        .setIcon(null)
        .setUpdatedBy(member.getAccount());
    } else {
      if (!ImageEncoder.supports(icon.getContentType())) {
        throw Status.BAD_REQUEST.get();
      }

      try {
        team
          .setIcon(ImageEncoder.encodeToString(icon.getBytes()))
          .setUpdatedBy(member.getAccount());
      } catch (IOException e) {
        throw Status.BAD_REQUEST.get();
      }
    }

    return new TeamDTO(teamJPA.save(team), true);
  }

  public void delete(@NonNull Long accountId,
                     @NonNull Long teamId) {
    var member = teamMembersJPA
      .findByAccountIdAndTeamIdAndOperator(accountId, teamId, true)
      .orElseThrow(Status.UNAUTHORIZED);

    // TODO delete everything
    teamJPA.delete(member.getTeam());
  }


}
