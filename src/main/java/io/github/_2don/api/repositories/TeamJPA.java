package io.github._2don.api.repositories;

import io.github._2don.api.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamJPA extends JpaRepository<Team, Long> {
}
