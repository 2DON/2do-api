package io.github._2don.api.teammember;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMemberDTO {

  private Long accountId;
  private Boolean operator;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public TeamMemberDTO(@NonNull TeamMember teamMember) {
    this(
      teamMember.getAccount().getId(),
      teamMember.getOperator(),
      teamMember.getCreatedAt(),
      teamMember.getCreatedBy().getId(),
      teamMember.getUpdatedAt(),
      teamMember.getUpdatedBy().getId()
    );
  }

}
