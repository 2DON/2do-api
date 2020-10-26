package io.github._2don.api.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @ManyToOne
  @JsonIgnore
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(referencedColumnName = "id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Project project;

  @Column(nullable = false)
  private Integer ordinal = Integer.MAX_VALUE;

  @NotNull
  @Size(min = 1, max = 80)
  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private TaskStatus status = TaskStatus.IN_PROGRESS;

  @Column(columnDefinition = "TEXT")
  private String options;

  @ManyToOne
  @JsonIdentityReference(alwaysAsId = true)
  @JoinColumn(name = "assigned_to", referencedColumnName = "id")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

  public Task(Project project, @NotNull @Size(min = 1, max = 80) String description, Account assignedTo) {
    this.project = project;
    this.description = description;
    this.assignedTo = assignedTo;
  }
}
