package io.github._2don.api.projectmember;

import io.github._2don.api.account.Account;
import io.github._2don.api.project.Project;
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
@IdClass(ProjectMemberId.class)
public class ProjectMember {

  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Account account;

  @Id
  @ManyToOne
  private Project project;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Team team;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private ProjectMemberPermissions permissions;

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

}
