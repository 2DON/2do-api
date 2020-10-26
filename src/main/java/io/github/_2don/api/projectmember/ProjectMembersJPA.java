package io.github._2don.api.projectmember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMembersJPA extends JpaRepository<ProjectMembers, ProjectMembersId> {

  boolean existsByAccountId(Long accountId);

  List<ProjectMembers> findAllByAccountId(Long accountId);

  boolean existsByAccountIdAndProjectId(Long accountId, Long projectId);

  Optional<ProjectMembers> findByAccountIdAndProjectId(Long accountId, Long projectId);

}
