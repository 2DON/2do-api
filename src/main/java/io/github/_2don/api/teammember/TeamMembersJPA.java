package io.github._2don.api.teammember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMembersJPA extends JpaRepository<TeamMember, TeamMembersId> {

  List<TeamMember> findAllByAccountId(Long accountId);

  List<TeamMember> findAllByTeamId(Long teamId);

  boolean existsByAccountIdAndTeamId(Long accountId, Long teamId);

  Optional<TeamMember> findByAccountIdAndTeamId(Long accountId, Long teamId);

  Long countByTeamIdAndOperator(Long teamId, Boolean operator);
}
