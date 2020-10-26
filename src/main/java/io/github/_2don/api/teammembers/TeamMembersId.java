package io.github._2don.api.teammembers;

import io.github._2don.api.account.Account;
import io.github._2don.api.team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TeamMembersId implements Serializable {

  private Account account;
  private Team team;

}
