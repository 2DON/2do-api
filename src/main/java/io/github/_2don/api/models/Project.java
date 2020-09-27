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

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

  @Id
  @GeneratedValue
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @NotNull
  @Size
  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Byte status;

  @Column(nullable = false)
  private String observation;

  @Column(nullable = false)
  private Boolean archived;

  @Column(nullable = false)
  private String options;

  @Column(nullable = false)
  private Timestamp createdAt;

  @Column(nullable = false)
  private Timestamp updatedAt;


  @Column(nullable = false)
  private Account createdBy;

  @Column(nullable = false)
  private Account updatedBy;

}
