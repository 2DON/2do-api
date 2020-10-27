package io.github._2don.api.team;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.teammember.TeamMember;
import io.github._2don.api.teammember.TeamMembersJPA;
import io.github._2don.api.utils.ImageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teams")
public class TeamController {

  @Autowired
  private TeamJPA teamJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private TeamMembersJPA teamMembersJPA;

  @GetMapping
  public List<Team> index(@AuthenticationPrincipal Long accountId) {

    return teamMembersJPA.findAllByAccountId(accountId).stream().map(TeamMember::getTeam)
      .collect(Collectors.toList());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Team store(@AuthenticationPrincipal Long accountId, @Valid @RequestBody Team team) {

    Account account = accountJPA.findById(accountId).orElse(null);
    if (account == null || !account.getPremium()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    team.setCreatedBy(account);
    team.setUpdatedBy(account);

    var teamCreated = teamJPA.save(team);

    TeamMember teamMembers = new TeamMember(account, teamCreated);
    teamMembers.setOperator(true);
    teamMembersJPA.save(teamMembers);

    return teamCreated;
  }

  @GetMapping("/{teamId}")
  public Team show(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId) {
    if (!teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    return teamJPA.findById(teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  @PatchMapping("/{teamId}")
  public Team edit(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId,
                   @RequestPart(name = "name", required = false) String name,
                   @RequestPart(name = "removeAvatar", required = false) String removeAvatar,
                   @RequestPart(name = "avatar", required = false) MultipartFile avatar) throws IOException {
    var teamMeta = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (!teamMeta.getOperator()) {
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

      team.setAvatarUrl(ImageEncoder.encodeToString(avatar.getBytes()));
    } else if (removeAvatar != null) {
      team.setAvatarUrl(null);
    }

    team.setUpdatedBy(accountJPA.getOne(accountId));
    return teamJPA.save(team);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId) {
    var teamMeta = teamMembersJPA.findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    if (!teamMeta.getOperator()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // TODO just delete?
    teamJPA.delete(teamJPA.getOne(teamId));
  }

}
