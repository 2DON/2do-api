package io.github._2don.api.repositories;

import io.github._2don.api.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectJPA extends JpaRepository<Project, Long>{
//  List<Project> findAllByAccountIdAndArchived(Long accountId, Boolean archived);
}
