package io.github._2don.api.projectmember;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ProjectMemberJPA extends JpaRepository<ProjectMember, ProjectMemberId> {

  boolean existsByAccountIdAndProjectId(Long accountId, Long projectId);

  Long countByProjectId(Long projectId);

  Long countByAccountId(Long accountId);

  Long countByAccountIdAndPermission(Long projectId, ProjectMemberPermission permission);

  Optional<ProjectMember> findByAccountIdAndProjectId(Long accountId, Long projectId);

  Optional<ProjectMember> findByProjectIdAndPermission(Long projectId, ProjectMemberPermission permission);

  List<ProjectMember> findAllByProjectId(Long projectId);

  List<ProjectMember> findAllByAccountIdAndProjectArchived(Long accountId, boolean archived);

  Optional<ProjectMember> findByAccountIdAndProjectIdAndProjectArchived(Long accountId, Long projectId, boolean archived);

  @Transactional
  long deleteByProjectId(Long taskId);
}
