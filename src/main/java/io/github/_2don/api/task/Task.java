package io.github._2don.api.task;

import io.github._2don.api.account.Account;
import io.github._2don.api.project.Project;
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
public class Task {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Project project;

  @Column(nullable = false)
  private Integer ordinal = Integer.MAX_VALUE;

  @NotNull
  @Size(min = 1, max = 80)
  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskStatus status = TaskStatus.IN_PROGRESS;

  @Column(columnDefinition = "TEXT")
  private String options;

  @ManyToOne
  @JoinColumn(name = "assigned_to", referencedColumnName = "id")
  private Account assignedTo;

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

  public Task(Project project, @NotNull @Size(min = 1, max = 80) String description,
              Account assignedTo) {
    this.project = project;
    this.description = description;
    this.assignedTo = assignedTo;
  }
}
