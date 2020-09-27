package io.github._2don.api.models;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMembers {

  @NotNull
  @Column(nullable = false)
  private Project projectId;

  @NotNull
  @Column(nullable = false)
  private Account accountId;

  @NotNull
  @Column(nullable = false)
  private Team teamId;


  @NotNull
  @Column(nullable = false)
  private Boolean permissions;

  @NotNull
  @Column(nullable = false)
  private Timestamp createdAt;

  @NotNull
  @Column(nullable = false)
  private Timestamp updatedAt;
}
