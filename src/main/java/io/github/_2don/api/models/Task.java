package io.github._2don.api.models;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
public class Task {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  @Size(min = 4, max = 80)
  private String description;

  @NotNull
  @Column(nullable = false)
  private Boolean status;

  @Column
  private String options;

  @Column
  private Timestamp createdAt;

  @Column
  private Timestamp updatedAt;

  @Column(nullable = false)
  private Project projectId;

  @Column(nullable = false)
  private Account assingedTo;

  @Column(nullable = false)
  private Account createdBy;

  @Column(nullable = false)
  private Account updateBy;



}
