package io.github._2don.api.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

  @Autowired
  private TeamService teamService;

  @GetMapping
  public List<TeamDTO> index(@AuthenticationPrincipal Long accountId) {
    return teamService.findTeams(accountId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TeamDTO store(@AuthenticationPrincipal Long accountId,
                       @RequestPart(name = "name") String name) {
    return teamService.create(accountId, name);
  }

  @GetMapping("/{teamId}")
  public ResponseEntity<TeamDTO> show(@AuthenticationPrincipal Long accountId,
                                      @PathVariable Long teamId) {
    return ResponseEntity.of(teamService.find(accountId, teamId));
  }

  @PatchMapping("/{teamId}")
  public TeamDTO update(@AuthenticationPrincipal Long accountId,
                        @PathVariable Long teamId,
                        @RequestPart(name = "name") String name) {
    return teamService.update(accountId, teamId, name);
  }

  @PutMapping("/{teamId}/icon")
  public TeamDTO updateIcon(@AuthenticationPrincipal Long accountId,
                            @PathVariable Long teamId,
                            @RequestPart(name = "icon", required = false) MultipartFile icon) {
    return teamService.updateIcon(accountId, teamId, icon);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable Long teamId) {
    teamService.delete(accountId, teamId);
  }

}
