package io.github._2don.api.teammember;

import com.fasterxml.jackson.annotation.*;
import io.github._2don.api.account.Account;
import io.github._2don.api.team.Team;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@IdClass(TeamMembersId.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMember {

  @Id
  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account account;

  @Id
  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Team team;

  @NotNull
  @Column(nullable = false)
  private Boolean operator = false;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

  public TeamMember(Account account, Team team) {
    this.account = account;
    this.team = team;
  }

}
