package io.github._2don.api.step;

import io.github._2don.api.task.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StepJPA extends JpaRepository<Step, Long> {

  List<Step> findAllByTaskId(Long taskId, Sort sort);

  boolean existsByIdAndTaskId(Long stepId, Long taskId);

  Optional<Step> findByIdAndTaskId(Long stepId, Long taskId);

  Optional<Step> findByIdAndTaskIdAndTaskProjectId(Long stepId, Long taskId, Long projectId);

}
