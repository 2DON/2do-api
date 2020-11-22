package io.github._2don.api.task;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

  @JsonIdentityReference(alwaysAsId = true)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @ManyToOne
  @JoinColumn(name = "assigned_to", referencedColumnName = "id")
  private Account assignedTo;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp createdAt;

  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp updatedAt;

  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "updated_by", referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Account updatedBy;

  public Task(Project project, @NotNull @Size(min = 1, max = 80) String description,
              Account assignedTo) {
    this.project = project;
    this.description = description;
    this.assignedTo = assignedTo;
  }
}
