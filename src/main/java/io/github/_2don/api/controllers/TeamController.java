package io.github._2don.api.controllers;

import io.github._2don.api.models.Team;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.repositories.TeamJPA;
import io.github._2don.api.utils.ImageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
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

  @PatchMapping("/{teamId}")
  public Team edit(@AuthenticationPrincipal Long accountId,
                   @PathVariable("teamId") Long teamId,
                   @RequestPart(name = "name", required = false) String name,
                   @RequestPart(name = "removeAvatar", required = false) String removeAvatar,
                   @RequestPart(name = "avatar", required = false) MultipartFile avatar) throws IOException {
    // TODO verify if user is part of the project
    // TODO verify if user has permission

    var team = teamJPA.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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
