package io.github._2don.api.repositories;

import io.github._2don.api.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJPA extends JpaRepository<Project, Long> {
}
