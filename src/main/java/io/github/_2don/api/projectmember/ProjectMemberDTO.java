package io.github._2don.api.projectmember;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github._2don.api.account.Account;
import io.github._2don.api.team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ProjectMemberDTO {
  static ProjectMemberDTO from(ProjectMember projectMember) {
    return new ProjectMemberDTO.Impl(
      projectMember.getAccount(),
      projectMember.getTeam(),
      projectMember.getPermission(),
      projectMember.getCreatedAt(),
      projectMember.getCreatedBy(),
      projectMember.getUpdatedAt(),
      projectMember.getUpdatedBy()
    );
  }

  // fixme already save just the id, right?
  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Account getAccount();

  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Team getTeam();

  ProjectMemberPermission getPermission();

  Timestamp getCreatedAt();

  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Account getCreatedBy();

  Timestamp getUpdatedAt();

  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Account getUpdatedBy();

  @Data
  @AllArgsConstructor
  class Impl implements ProjectMemberDTO {
    private Account account;
    private Team team;
    private ProjectMemberPermission permission;
    private Timestamp createdAt;
    private Account createdBy;
    private Timestamp updatedAt;
    private Account updatedBy;
  }
}
