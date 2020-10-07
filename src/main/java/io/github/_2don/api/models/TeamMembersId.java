package io.github._2don.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMembersId implements Serializable {

  private Account account;
  private Team team;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TeamMembersId that = (TeamMembersId) o;

    if (!Objects.equals(account, that.account)) return false;
    return Objects.equals(team, that.team);
  }

  @Override
  public int hashCode() {
    int result = account != null ? account.hashCode() : 0;
    result = 31 * result + (team != null ? team.hashCode() : 0);
    return result;
  }
}
