package io.github._2don.api.teammember;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TeamMemberService {

  @Autowired
  private TeamMembersJPA teamMembersJPA;

  public void assertIsMember(@NonNull Long accountId,
                             @NonNull Long teamId) {
    if (!teamMembersJPA.existsByAccountIdAndTeamId(accountId, teamId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

}
