package io.github._2don.api.step;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface StepJPA extends JpaRepository<Step, Long> {

  List<Step> findAllByTaskId(Long taskId, Sort sort);

  boolean existsByIdAndTaskId(Long stepId, Long taskId);

  Optional<Step> findByIdAndTaskId(Long stepId, Long taskId);

  Optional<Step> findByIdAndTaskIdAndTaskProjectId(Long stepId, Long taskId, Long projectId);

  @Transactional
  long deleteByTaskId(Long taskId);

}
