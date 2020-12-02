package io.github._2don.api.stats;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectStats {

  private final ProjectDayStats[] week = new ProjectDayStats[]{
    new ProjectDayStats(),
    new ProjectDayStats(),
    new ProjectDayStats(),
    new ProjectDayStats(),
    new ProjectDayStats(),
    new ProjectDayStats(),
    new ProjectDayStats()
  };

  public ProjectDayStats getDay(int day) {
    return week[day];
  }

}
