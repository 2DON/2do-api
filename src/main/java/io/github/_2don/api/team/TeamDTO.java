package io.github._2don.api.team;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github._2don.api.teammember.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDTO {

  private Long id;
  private String icon;
  private String name;
  private Boolean operator;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public TeamDTO(@NonNull Team team, @NonNull Boolean operator) {
    this(
      team.getId(),
      team.getIcon(),
      team.getName(),
      operator,
      team.getCreatedAt(),
      team.getCreatedBy().getId(),
      team.getUpdatedAt(),
      team.getUpdatedBy().getId()
    );
  }

  public TeamDTO(@NonNull TeamMember teamMember) {
    this(teamMember.getTeam(), teamMember.getOperator());
  }

}
