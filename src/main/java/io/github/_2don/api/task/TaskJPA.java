package io.github._2don.api.task;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TaskJPA extends JpaRepository<Task, Long> {

  List<Task> findAllByProjectId(Long projectId, Sort sort);

  boolean existsByIdAndProjectId(Long taskId, Long projectId);

  Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);

  @Transactional
  long deleteByProjectId(Long taskId);

}
