package io.github._2don.api.models;

import com.fasterxml.jackson.annotation.*;
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
@IdClass(ProjectMembersId.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMembers {

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
  private Project project;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Team team;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private ProjectMembersPermissions permissions;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

  public ProjectMembers(Account account,
                        Project project,
                        Team team,
                        ProjectMembersPermissions permissions) {
    this.account = account;
    this.project = project;
    this.team = team;
    this.permissions = permissions;
  }
}
