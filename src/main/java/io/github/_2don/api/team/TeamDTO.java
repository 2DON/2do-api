package io.github._2don.api.team;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github._2don.api.account.Account;
import io.github._2don.api.task.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDTO {

  private Long id;
  private String avatarUrl;
  private String name;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public TeamDTO(@NonNull Team team) {
    this(
      team.getId(),
      team.getAvatarUrl(),
      team.getName(),
      team.getCreatedAt(),
      team.getCreatedBy().getId(),
      team.getUpdatedAt(),
      team.getUpdatedBy().getId()
    );
  }

}
