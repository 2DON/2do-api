package io.github._2don.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github._2don.api.models.Team;

public interface TeamJPA extends JpaRepository<Team, Long>{
    
}
