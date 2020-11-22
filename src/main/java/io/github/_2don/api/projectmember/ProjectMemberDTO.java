package io.github._2don.api.projectmember;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMemberDTO {

  private Long accountId;
  private Long teamId;
  private ProjectMemberPermission permission;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public ProjectMemberDTO(@NonNull ProjectMember projectMember) {
    this(
      projectMember.getAccount().getId(),
      projectMember.getTeam() == null ? null : projectMember.getTeam().getId(),
      projectMember.getPermission(),
      projectMember.getCreatedAt(),
      projectMember.getCreatedBy().getId(),
      projectMember.getUpdatedAt(),
      projectMember.getUpdatedBy().getId()
    );
  }

}
