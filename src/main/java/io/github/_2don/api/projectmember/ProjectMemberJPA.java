package io.github._2don.api.projectmember;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberJPA extends JpaRepository<ProjectMember, ProjectMemberId> {

  boolean existsByAccountId(Long accountId);

  boolean existsByAccountIdAndProjectId(Long accountId, Long projectId);

  Long countByProjectId(Long projectId);

  Long countByAccountId(Long accountId);

  Long countByAccountIdAndPermissions(Long projectId, ProjectMemberPermissions permissions);

  Optional<ProjectMember> findByAccountIdAndProjectId(Long accountId, Long projectId);

  Optional<ProjectMember> findByProjectIdAndPermissions(Long projectId, ProjectMemberPermissions permissions);

  Optional<ProjectMember> findByAccountIdAndProjectIdAndPermissions(Long accountId, Long projectId, ProjectMemberPermissions permissions);

  List<ProjectMember> findAllByAccountId(Long accountId);

  List<ProjectMemberDTO> findAllByProjectId(Long projectId);
}
