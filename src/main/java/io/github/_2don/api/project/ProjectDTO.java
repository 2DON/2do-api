package io.github._2don.api.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github._2don.api.projectmember.ProjectMember;
import io.github._2don.api.projectmember.ProjectMemberPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDTO {

  private Long id;
  private String icon;
  private Integer ordinal;
  private String description;
  private String observation;
  private Boolean archived;
  private String options;
  private ProjectMemberPermission accessLevel;
  private Timestamp createdAt;
  private Long createdBy;
  private Timestamp updatedAt;
  private Long updatedBy;

  public ProjectDTO(@NonNull Project project,
                    ProjectMemberPermission permission) {
    this(
      project.getId(),
      project.getIcon(), 
      project.getOrdinal(),
      project.getDescription(),
      project.getObservation(),
      project.getAchieved(),
      project.getOptions(),
      permission,
      project.getCreatedAt(),
      project.getCreatedBy().getId(),
      project.getUpdatedAt(),
      project.getUpdatedBy().getId()
    );
  }

  public ProjectDTO(@NonNull ProjectMember projectMember) {
    this(projectMember.getProject(), projectMember.getPermission());
  }
}
