package io.github._2don.api.teammember;

import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.team.Team;
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

  @NonNull
  public TeamMember getMeta(Long accountId, Long teamId) {
    return teamMembersJPA
      .findByAccountIdAndTeamId(accountId, teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  @NonNull
  public TeamMember edit(@NonNull Long accountId,
                   @NonNull Long memberId,
                   @NonNull Long teamId,
                   @NonNull Boolean operator){

    var loggedMeta = getMeta(accountId, teamId);
    var accountMeta = getMeta(memberId, teamId);

    if(!loggedMeta.getOperator()){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (teamMembersJPA.countByTeamIdAndOperator(teamId, true) < 2) {
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }

    accountMeta.setOperator(operator).setUpdatedBy(loggedMeta.getAccount());
    return teamMembersJPA.save(accountMeta);
  }

}
