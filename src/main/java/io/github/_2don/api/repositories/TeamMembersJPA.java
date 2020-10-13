package io.github._2don.api.repositories;

import io.github._2don.api.models.TeamMembers;
import io.github._2don.api.models.TeamMembersId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMembersJPA extends JpaRepository<TeamMembers, TeamMembersId> {

  boolean existsByAccountIdAndTeamId(Long accountId, Long teamId);

  Optional<TeamMembers> findByAccountIdAndTeamId(Long accountId, Long teamId);

}
