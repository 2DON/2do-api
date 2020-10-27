package io.github._2don.api.teammember;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembersJPA extends JpaRepository<TeamMember, TeamMembersId> {

  List<TeamMember> findAllByAccountId(Long accountId);

  List<TeamMember> findAllByTeamId(Long teamId);

  boolean existsByAccountIdAndTeamId(Long accountId, Long teamId);

  Optional<TeamMember> findByAccountIdAndTeamId(Long accountId, Long teamId);

}
