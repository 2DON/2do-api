package io.github._2don.api.step;

import io.github._2don.api.task.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StepJPA extends JpaRepository<Step, Long> {

  List<Step> findAllByTaskId(Long taskId, Sort sort);

  boolean existsByIdAndTaskId(Long taskId, Long projectId);

  Optional<Task> findByIdAndTaskId(Long taskId, Long projectId);

}
