package io.github._2don.api.repositories;

import io.github._2don.api.models.ProjectMembers;
import io.github._2don.api.models.ProjectMembersId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMembersJPA extends JpaRepository<ProjectMembers, ProjectMembersId> {

  boolean existsByAccountIdAndProjectId(Long accountId, Long projectId);

  Optional<ProjectMembers> findByAccountIdAndProjectId(Long accountId, Long projectId);

}
