package io.github._2don.api.step;

import io.github._2don.api.account.Account;
import io.github._2don.api.task.Task;
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
public class Step {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private Task task;

  @Column(nullable = false)
  private Integer ordinal = Integer.MAX_VALUE;

  @NotNull
  @Size(min = 1, max = 80)
  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private StepStatus status = StepStatus.IN_PROGRESS;

  @Column(length = 250)
  private String observation;

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

  public Step(Task task, @NotNull @Size(min = 1, max = 80) String description) {
    this.task = task;
    this.description = description;
  }
}
