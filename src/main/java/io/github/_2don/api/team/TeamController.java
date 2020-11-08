package io.github._2don.api.team;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
