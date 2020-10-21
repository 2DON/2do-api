package io.github._2don.api.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github._2don.api.models.Step;

import java.util.List;

public interface StepJPA extends JpaRepository<Step, Long> {

  List<Step> findAllByTaskId(Long taskId, Sort sort);}
