package io.github._2don.api.teammember;

import com.fasterxml.jackson.annotation.*;
import io.github._2don.api.account.Account;
import io.github._2don.api.team.Team;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@IdClass(TeamMembersId.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMember {

  @Id
  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Account account;

  @Id
  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Team team;

  @NotNull
  @Column(nullable = false)
  private Boolean operator = false;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp createdAt;

  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account updatedBy;

  public TeamMember(Account account, Team team) {
    this.account = account;
    this.team = team;
  }

}
