package io.github._2don.api.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {

  private Long id;
  private Integer ordinal;
  private String description;
  private TaskStatus status;
  private String options;
  private Long assignedTo;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public TaskDTO(@NonNull Task task) {
    this(
      task.getId(),
      task.getOrdinal(),
      task.getDescription(),
      task.getStatus(),
      task.getOptions(),
      task.getAssignedTo() == null ? null : task.getAssignedTo().getId(),
      task.getCreatedAt(),
      task.getCreatedBy().getId(),
      task.getUpdatedAt(),
      task.getUpdatedBy().getId()
    );
  }

}
