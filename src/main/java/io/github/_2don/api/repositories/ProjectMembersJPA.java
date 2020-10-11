package io.github._2don.api.repositories;

import io.github._2don.api.models.ProjectMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github._2don.api.models.ProjectMembers;

public interface ProjectMembersJPA extends JpaRepository<ProjectMembers, ProjectMembersId>{

}
