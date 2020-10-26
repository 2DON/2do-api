package io.github._2don.api.projectmember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberJPA extends JpaRepository<ProjectMember, ProjectMemberId> {

  boolean existsByAccountId(Long accountId);

  boolean existsByAccountIdAndProjectId(Long accountId, Long projectId);

  Long countByProjectId(Long projectId);

  Long countByAccountId(Long accountId);

  Long countByAccountIdAndPermissions(Long projectId, int permissions);

  Optional<ProjectMember> findByAccountIdAndProjectId(Long accountId, Long projectId);

  Optional<ProjectMember> findByProjectIdAndPermissions(Long projectId, int permissions);

  Optional<ProjectMember> findByAccountIdAndProjectIdAndPermissions(Long accountId, Long projectId, int permissions);

  List<ProjectMember> findAllByAccountId(Long accountId);

  List<ProjectMemberDTO> findAllByProjectId(Long projectId);
}
