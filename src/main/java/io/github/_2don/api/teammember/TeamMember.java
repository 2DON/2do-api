package io.github._2don.api.teammember;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountToPublicAccountConverter;
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
  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  @JsonSerialize(converter = AccountToPublicAccountConverter.class)
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

  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonSerialize(converter = AccountToPublicAccountConverter.class)
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonSerialize(converter = AccountToPublicAccountConverter.class)
  @JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account updatedBy;

  public TeamMember(Account account, Team team) {
    this.account = account;
    this.team = team;
  }

}
