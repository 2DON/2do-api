package io.github._2don.api.stats;

import io.github._2don.api.projectmember.ProjectMemberJPA;
import io.github._2don.api.projectmember.ProjectMemberService;
import io.github._2don.api.step.StepJPA;
import io.github._2don.api.task.TaskJPA;
import io.github._2don.api.utils.TimeUtils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class ProjectStatsService {

  private static final long DAY = TimeUnit.DAYS.toMillis(1);

  @Autowired
  private StepJPA stepJPA;
  @Autowired
  private TaskJPA taskJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private ProjectMemberService projectMemberService;

  private int weekIndexOf(Timestamp timestamp,
                          long weekStart) {
    if (timestamp == null) {
      return -1;
    }

    return weekIndexOf(timestamp.getTime(), weekStart);
  }

  private int weekIndexOf(long timestamp,
                          long weekStart) {
    var diff = timestamp - weekStart;

    var index = -1;

    while (diff >= 0) {
      diff -= DAY;
      index += 1;
    }

    return index;
  }

  public ProjectStats stats(@NonNull Long accountId,
                            @NonNull Long projectId) {
    projectMemberService.assertIsMember(accountId, projectId, HttpStatus.UNAUTHORIZED);

    var weekStart = TimeUtils.nowLessWeeks(1L);
    var stats = new ProjectStats();

    for (long day = weekStart, index = 0;
         day <= Instant.now().toEpochMilli();
         day += DAY, index++) {
      var date = new Date(day);

      stats.getDay((int) index).setDate(date);
    }

    stepJPA
      .findAllByTaskProjectId(projectId)
      .forEach(step -> {
        var indexCreated = weekIndexOf(step.getCreatedAt(), weekStart);
        var indexUpdated = weekIndexOf(step.getUpdatedAt(), weekStart);

        if (indexCreated != -1) {
          stats.getDay(indexCreated).createdPlusOne(step.getCreatedBy().getId());
        }

        if (indexUpdated != -1) {
          stats.getDay(indexUpdated).updatedPlusOne(step.getUpdatedBy().getId());
        }
      });

    taskJPA
      .findAllByProjectId(projectId, Sort.unsorted())
      .forEach(task -> {
        var indexCreated = weekIndexOf(task.getCreatedAt(), weekStart);
        var indexUpdated = weekIndexOf(task.getUpdatedAt(), weekStart);

        if (indexCreated != -1) {
          stats.getDay(indexCreated).createdPlusOne(task.getCreatedBy().getId());
        }

        if (indexUpdated != -1) {
          stats.getDay(indexUpdated).updatedPlusOne(task.getUpdatedBy().getId());
        }
      });

    return stats;
  }

}
