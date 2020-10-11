package io.github._2don.api.controllers;

import io.github._2don.api.models.Team;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.TeamJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

  private @Autowired
  TeamJPA teamJPA;
  private @Autowired
  AccountJPA accountJPA;

  @GetMapping
  public List<Team> index() {
    // TODO just for testing, REDO

    return teamJPA.findAll();
  }

  @GetMapping("/{teamId}")
  public ResponseEntity<Team> show(@PathVariable("teamId") Long teamId) {
    // TODO verify if user is part of the project

    return ResponseEntity.of(teamJPA.findById(teamId));
  }

  @PutMapping("/{teamId}")
  public Team override(@AuthenticationPrincipal Long accountId,
                       @PathVariable("teamId") Long teamId, @Valid @RequestBody Team team) {
    // TODO verify if user is part of the project
    // TODO verify if user has permission

    var account = accountJPA.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE));
    var teamEdit = teamJPA.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    teamEdit.setName(team.getName());
    // TODO recieve image (MultipartFile)
    teamEdit.setAvatarUrl(team.getAvatarUrl());
    teamEdit.setUpdatedBy(account);

    return teamJPA.save(teamEdit);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Team store(@AuthenticationPrincipal Long accountId,
                    @Valid @RequestBody Team team) {
    var account = accountJPA.getOne(accountId);

    team.setCreatedBy(account);
    team.setUpdatedBy(account);

    return teamJPA.save(team);
  }

  @DeleteMapping("/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @PathVariable("teamId") Long teamId) {
    // TODO verify if user is part of the project
    // TODO verify if user has permission
    // TODO verify if project exists

    teamJPA.delete(teamJPA.getOne(teamId));
  }

}
