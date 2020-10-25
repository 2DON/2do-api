package io.github._2don.api.repositories;

import io.github._2don.api.models.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskJPA extends JpaRepository<Task, Long> {

  Boolean existsByTaskIdAndProjectId(Long taskId, Long projectId);

  List<Task> findAllByProjectId(Long projectId, Sort sort);
}
