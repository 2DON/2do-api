package io.github._2don.api.projectmember;

import com.fasterxml.jackson.annotation.*;
import io.github._2don.api.account.Account;
import io.github._2don.api.project.Project;
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
@IdClass(ProjectMemberId.class)
public class ProjectMember {

  @Id
  @Column(name = "account_id")
  private Long accountId;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  private Account account;

  @Id
  @JsonIgnore
  @Column(name = "project_id")
  private Long projectId;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  private Project project;

  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @ManyToOne
  @JoinColumn(referencedColumnName = "id", insertable = false, updatable = false)
  private Team team;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ProjectMemberPermission permission;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp createdAt;

  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Timestamp updatedAt;

  @ManyToOne
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account updatedBy;

  public boolean canNot(ProjectMemberPermission permissions) {
    return this.permission.compareTo(permissions) < 0;
  }

}
