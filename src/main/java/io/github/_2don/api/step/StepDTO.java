package io.github._2don.api.step;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StepDTO {

  private Long id;
  private Integer ordinal;
  private String description;
  private StepStatus status;
  private String observation;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public StepDTO(@NonNull Step step) {
    this(
      step.getId(),
      step.getOrdinal(),
      step.getDescription(),
      step.getStatus(),
      step.getObservation(),
      step.getCreatedAt(),
      step.getCreatedBy().getId(),
      step.getUpdatedAt(),
      step.getUpdatedBy().getId()
    );
  }

}
