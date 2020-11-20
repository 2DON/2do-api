package io.github._2don.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

  @Autowired
  private TeamService teamService;

  @GetMapping
  public List<Team> index(@AuthenticationPrincipal Long accountId) {
    return teamService.getTeams(accountId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Team store(@AuthenticationPrincipal Long accountId, @Valid @RequestBody Team team) {
    return teamService.add(accountId, team);
  }

  @GetMapping("/{teamId}")
  public Team show(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId) {
    return teamService.getTeam(accountId, teamId);
  }

  @PatchMapping("/{teamId}")
  public Team edit(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId,
                   @RequestPart(name = "name", required = false) String name,
                   @RequestPart(name = "removeAvatar", required = false) String removeAvatar,
                   @RequestPart(name = "avatar", required = false) MultipartFile avatar) throws IOException {
    return teamService.update(accountId, teamId, name, removeAvatar, avatar);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @PathVariable Long teamId) {
    teamService.delete(accountId, teamId);
  }

}
