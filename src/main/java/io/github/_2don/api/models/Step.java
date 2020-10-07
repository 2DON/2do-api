package io.github._2don.api.models;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Step {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  // TODO serialize just the task id
  // TODO LAZY?
  @JsonIgnore
  @ManyToOne
  @JoinColumn(referencedColumnName = "id", nullable = false)
  private Task task;

  @Column(nullable = false)
  private Integer ordinal = -1;

  @NotNull
  @Size(min = 1, max = 80)
  @Column(nullable = false)
  private String description;

  // TODO: enum StepStatus or the same as TaskStatus
  @Column(nullable = false)
  private Byte status;

  @Column(length = 250)
  private String observation;

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
