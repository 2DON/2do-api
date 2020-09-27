package io.github._2don.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github._2don.api.models.TeamMembers;

public interface TeamMembersJPA extends JpaRepository<TeamMembers, Long> {

}
