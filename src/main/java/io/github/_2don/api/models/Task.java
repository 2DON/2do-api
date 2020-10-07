package io.github._2don.api.models;

import java.sql.Timestamp;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  // TODO serialize just the project id
  // TODO LAZY?
  @JsonIgnore
  @ManyToOne
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Project project;

  @Column(nullable = false)
  private Integer ordinal = -1;

  @NotNull
  @Size(min = 1, max = 80)
  @Column(nullable = false)
  private String description;

  // TODO: enum TaskStatus
  @Column(nullable = false)
  private Byte status;

  @Column(columnDefinition = "TEXT")
  private String options;

  // TODO serialize just the account id
  @ManyToOne
  @JoinColumn(name="assigned_to", referencedColumnName = "id", nullable = false)
  private Account assignedTo;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp createdAt;

  @ManyToOne
  @JoinColumn(name="created_by", referencedColumnName = "id", nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Account createdBy;

  @UpdateTimestamp
  @Column(nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Timestamp updatedAt;

  @ManyToOne
  @JsonProperty(access = Access.READ_ONLY)
  @JoinColumn(name="updated_by", referencedColumnName = "id", nullable = false)
  private Account updatedBy;


}
