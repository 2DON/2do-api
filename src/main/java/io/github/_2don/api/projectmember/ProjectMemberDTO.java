package io.github._2don.api.projectmember;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github._2don.api.account.Account;
import io.github._2don.api.team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ProjectMemberDTO {

  static ProjectMemberDTO from(ProjectMember projectMember) {
    return new ProjectMemberDTO.Impl(
      projectMember.getAccount(),
      projectMember.getTeam(),
      projectMember.getPermissions(),
      projectMember.getCreatedAt(),
      projectMember.getCreatedBy(),
      projectMember.getUpdatedAt(),
      projectMember.getUpdatedBy()
    );
  }

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Account getAccount();

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Team getTeam();

  ProjectMemberPermissions getPermissions();

  Timestamp getCreatedAt();

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Account getCreatedBy();

  Timestamp getUpdatedAt();

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  Account getUpdatedBy();

  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  class Impl implements ProjectMemberDTO {
    private Account account;
    private Team team;
    private ProjectMemberPermissions permissions;
    private Timestamp createdAt;
    private Account createdBy;
    private Timestamp updatedAt;
    private Account updatedBy;
  }

}
