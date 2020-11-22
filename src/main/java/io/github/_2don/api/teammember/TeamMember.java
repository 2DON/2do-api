package io.github._2don.api.teammember;

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
@IdClass(TeamMemberId.class)
public class TeamMember {

  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Account account;

  @Id
  @ManyToOne
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Team team;

  @NotNull
  @Column(nullable = false)
  private Boolean operator = false;

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
