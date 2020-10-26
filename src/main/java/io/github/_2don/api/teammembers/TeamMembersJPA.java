package io.github._2don.api.teammembers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMembersJPA extends JpaRepository<TeamMembers, TeamMembersId> {

  List<TeamMembers> findAllByAccountId(Long accountId);

  boolean existsByAccountIdAndTeamId(Long accountId, Long teamId);

  Optional<TeamMembers> findByAccountIdAndTeamId(Long accountId, Long teamId);

}
