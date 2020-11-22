package io.github._2don.api.team;

import io.github._2don.api.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
public class Team {

  @Id
  @GeneratedValue
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String avatarUrl;

  @NotNull
  @Size(min = 1, max = 45)
  @Column(nullable = false, length = 45)
  private String name;

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

  public Team(@NotNull @Size(min = 1, max = 45) String name) {
    this.name = name;
  }

}
