package io.github._2don.api.repositories;

import io.github._2don.api.models.TeamMembers;
import io.github._2don.api.models.TeamMembersId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembersJPA extends JpaRepository<TeamMembers, TeamMembersId> {

}
