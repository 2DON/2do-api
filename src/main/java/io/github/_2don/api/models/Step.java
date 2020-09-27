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
public class Step {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @NotNull
  @Size
  @Column(nullable = false)
  private String description;

  @NotNull
  @Column(nullable = false)
  private Byte status;

  @NotNull
  @Column(nullable = false)
  private String observation;

  @NotNull
  @Column(nullable = false)
  private Timestamp createdAt;

  @NotNull
  @Column(nullable = false)
  private Timestamp updateAt;


  @NotNull
  @Column(nullable = false)
  private Account createdBy;

  @NotNull
  @Column(nullable = false)
  private Account updateBy;
  private Task taskId;
}
