package io.github._2don.api.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@IdClass(ProjectMembersId.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMembers {

  // FIXME field is NOT NULL, but shout not be
  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account account;

  @Id
  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Project project;

  // FIXME field is NOT NULL, but shout not be
  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Team team;

  // TODO enum ProjectMembersPermissions
  @NotNull
  @Column(nullable = false)
  private Byte permissions;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

  public ProjectMembers(Account account, Project project, Team team) {
    this.account = account;
    this.project = project;
    this.team = team;
  }
}
