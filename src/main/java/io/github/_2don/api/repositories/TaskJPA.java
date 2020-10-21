package io.github._2don.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github._2don.api.models.Task;

public interface TaskJPA extends JpaRepository<Task, Long> {

  Boolean existsByTaskIdAndProjectId(Long taskId, Long projectId);
}
