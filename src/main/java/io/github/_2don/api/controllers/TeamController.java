package io.github._2don.api.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.github._2don.api.models.Account;
import io.github._2don.api.models.Team;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.TeamJPA;

@RestController
@RequestMapping("/teams")
public class TeamController {

  private @Autowired TeamJPA teamJPA;
  private @Autowired AccountJPA accountJPA;

  @GetMapping("/{teamId}")
  public ResponseEntity<Team> show(@PathVariable("teamId") Long teamId) {
    return ResponseEntity.of(teamJPA.findById(teamId));
  }

  @PutMapping("/{teamId}")
  public ResponseEntity<Team> edit(@AuthenticationPrincipal Long accountId,
      @PathVariable("teamId") Long teamId, @Valid @RequestBody Team team) {
    Account account = accountJPA.findById(accountId).orElse(null);
    Team teamEdit = teamJPA.findById(teamId).orElse(null);

    if (account == null) {
      return ResponseEntity.badRequest().build();
    } else if (teamEdit == null) {
      return ResponseEntity.notFound().build();
    }

    teamEdit.setName(team.getName());
    teamEdit.setAvatarURL(team.getAvatarURL());
    teamEdit.setUpdatedBy(account);

    teamEdit = teamJPA.save(teamEdit);

    return ResponseEntity.ok(teamEdit);
  }

  @PostMapping
  public ResponseEntity<Team> store(@AuthenticationPrincipal Long accountId,
      @Valid @RequestBody Team team) {

    Account account = accountJPA.findById(accountId).orElse(null);

    if (account == null) {
      return ResponseEntity.badRequest().build();
    } else if (team == null) {
      return ResponseEntity.notFound().build();
    }

    team.setCreatedBy(accountJPA.getOne(accountId));
    team.setUpdatedBy(accountJPA.getOne(accountId));

    team = teamJPA.save(team);

    return ResponseEntity.ok(team);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
      @PathVariable("teamId") Long teamId) {
    teamJPA.delete(teamJPA.getOne(teamId));
  }

}
