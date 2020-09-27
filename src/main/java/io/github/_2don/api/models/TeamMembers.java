package io.github._2don.api.models;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class TeamMembers {

  @NotNull
  @Size
  @Column(nullable = false)
  private Account accountId;

  @NotNull
  @Column(nullable = false)
  private Team teamId;


  @NotNull
  @Column(nullable = false)
  private Boolean operator;

  @NotNull
  @Column(nullable = false)
  private Timestamp createdAt;

  @NotNull
  @Column(nullable = false)
  private Timestamp updatedAt;


}
