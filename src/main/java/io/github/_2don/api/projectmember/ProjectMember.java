package io.github._2don.api.projectmember;

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
  @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  private Account account;

  @Id
  @Column(name = "project_id")
  private Long projectId;

  @ManyToOne
  @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  private Project project;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Team team;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ProjectMemberPermission permission;

  @CreationTimestamp
  @Column(nullable = false)
  private Timestamp createdAt;

  @ManyToOne
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  private Timestamp updatedAt;

  @ManyToOne
  @JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = false)
  private Account updatedBy;

  public boolean isOwner() {
    return ProjectMemberPermission.OWNER.equals(this.permission);
  }

}
