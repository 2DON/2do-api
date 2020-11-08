package io.github._2don.api.projectmember;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.project.Project;
import io.github._2don.api.team.TeamService;
import io.github._2don.api.teammember.TeamMemberService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectMemberService {

  private static final Long NON_PREMIUM_MEMBER_LIMIT = 5L;
  private static final Long NON_PREMIUM_PROJECT_LIMIT = 1L;
  private static final Long NON_PREMIUM_PARTICIPATION_LIMIT = 3L;

  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private TeamService teamService;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMemberService teamMemberService;

  public boolean hasProject(Account account, Project project) {
    return projectMemberJPA.existsByAccountIdAndProjectId(account.getId(), project.getId());
  }

  public void assertMemberLimit(@NonNull Long projectId) {
    var owner = getOwner(projectId);
    if (owner.isPresent() && !owner.get().getPremium()
      && projectMemberJPA.countByProjectId(projectId) >= NON_PREMIUM_MEMBER_LIMIT) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }
  }

  public void assertProjectLimit(@NonNull Long accountId) {
    var account = accountService.getAccount(accountId);
    assertProjectLimit(account);
  }

  public void assertProjectLimit(@NonNull Account account) {
    if (!account.getPremium() && projectMemberJPA.countByAccountIdAndPermissions(account.getId(),
      ProjectMemberPermissions.OWNER.ordinal()) >= NON_PREMIUM_PROJECT_LIMIT) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }
  }

  public void assertParticipationLimit(@NonNull Long accountId) {
    var account = accountService.getAccount(accountId);

    if (!account.getPremium()
      && projectMemberJPA.countByAccountId(accountId) >= NON_PREMIUM_PARTICIPATION_LIMIT) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }
  }

  public void assertIsMember(@NonNull Long accountId, @NonNull Long projectId) {
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  @NonNull
  public List<ProjectMemberDTO> list(@NonNull Long accountId, @NonNull Long projectId) {
    assertIsMember(accountId, projectId);

    return projectMemberJPA.findAllByProjectId(projectId);
  }

  @NonNull
  private ProjectMember getMeta(Long accountId, Long projectId) {
    return projectMemberJPA.findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  @NonNull
  private Optional<Account> getOwner(@NonNull Long projectId) {
    var meta = projectMemberJPA.findByProjectIdAndPermissions(projectId,
      ProjectMemberPermissions.OWNER.ordinal());

    return meta.map(ProjectMember::getAccount);
  }

  @NonNull
  public ProjectMemberDTO add(@NonNull Long loggedId, @NonNull Long projectId,
                              @NonNull Long accountId, Long teamId, @NonNull ProjectMemberPermissions permissions) {

    assertMemberLimit(projectId);
    accountService.assertExists(accountId, HttpStatus.NOT_FOUND);
    assertParticipationLimit(accountId);

    if (projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    var loggedMeta = getMeta(loggedId, projectId);
    var loggedPerm = loggedMeta.getPermissions();

    if (loggedPerm.compareTo(ProjectMemberPermissions.MAN_MEMBERS) < 0
      || loggedPerm.compareTo(permissions) < 0) {
      // not a member_manager+ or trying to apply permissions higher then himself
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var projectMember = new ProjectMember()
      .setAccountId(accountId)
      .setProjectId(projectId)
      .setPermissions(permissions)
      .setCreatedBy(loggedMeta.getAccount())
      .setUpdatedBy(loggedMeta.getAccount());

    if (teamId != null) {
      teamMemberService.assertIsMember(accountId, teamId);

      projectMember.setTeam(teamService.getTeam(teamId));
    }

    return ProjectMemberDTO.from(projectMemberJPA.save(projectMember));
  }

  @NonNull
  public ProjectMemberDTO edit(@NonNull Long loggedId, @NonNull Long projectId,
                               @NonNull Long accountId, Long teamId, ProjectMemberPermissions permissions) {

    var loggedMeta = getMeta(loggedId, projectId);
    var loggedPerm = loggedMeta.getPermissions();
    var accountMeta = getMeta(accountId, projectId);
    var accountPerm = accountMeta.getPermissions();

    if (permissions != null) {
      if (loggedPerm.compareTo(ProjectMemberPermissions.MAN_MEMBERS) < 0
        || loggedPerm.compareTo(accountPerm) < 0 || loggedPerm.compareTo(permissions) < 0) {
        // not a member_manager+ or trying to apply permissions higher then himself
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      accountMeta.setPermissions(permissions);
    }

    if (teamId != null) {
      teamMemberService.assertIsMember(accountId, teamId);

      accountMeta.setTeam(teamService.getTeam(teamId));
    }

    accountMeta.setUpdatedBy(loggedMeta.getAccount());

    return ProjectMemberDTO.from(projectMemberJPA.save(accountMeta));
  }

  public void delete(@NonNull Long loggedId, @NonNull Long projectId, @NonNull Long accountId) {

    var accountMeta = getMeta(accountId, projectId);
    var accountPerm = accountMeta.getPermissions();

    if (!loggedId.equals(accountId)) {
      var loggedMeta = getMeta(loggedId, projectId);
      var loggedPerm = loggedMeta.getPermissions();

      if (loggedPerm.compareTo(ProjectMemberPermissions.MAN_MEMBERS) < 0
        || loggedPerm.compareTo(accountPerm) < 0) {
        // not a member_manager+ or trying to apply permissions higher then himself
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
    } else if (accountPerm == ProjectMemberPermissions.OWNER) {
      // cannot remove the owner, the user needs to transfer ownership first
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }

    projectMemberJPA.delete(accountMeta);
  }

  public void transferOwnership(Long ownerId, Long projectId, Long newOwnerId) {
    if (ownerId.equals(newOwnerId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    var owner = projectMemberJPA
      .findByAccountIdAndProjectIdAndPermissions(ownerId, projectId,
        ProjectMemberPermissions.OWNER.ordinal())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    var newOwner = getMeta(newOwnerId, projectId);

    assertProjectLimit(newOwner.getAccount());

    owner.setPermissions(ProjectMemberPermissions.MAN_PROJECT).setUpdatedBy(owner.getAccount());

    newOwner.setPermissions(ProjectMemberPermissions.OWNER).setUpdatedBy(owner.getAccount());

    projectMemberJPA.save(owner);
    projectMemberJPA.save(newOwner);
  }

  public List<ProjectMember> getAllProjectMembers(Long accountId) {
    return projectMemberJPA.findAllByAccountId(accountId);
  }

  public Optional<ProjectMember> getProjectMember(Long accountId, Long projectId) {
    return projectMemberJPA.findByAccountIdAndProjectId(accountId, projectId);
  }

  public boolean exist(Long accountId, Long projectId) {
    return projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId);
  }

  public boolean exist(Project project) {
    return projectMemberJPA.existsByAccountId(project.getCreatedBy().getId());
  }

  public ProjectMember add(ProjectMember projectMember) {
    return projectMemberJPA.save(projectMember);
  }

  public ProjectMember add(Account account, Project project, ProjectMemberPermissions permission) {

    ProjectMember projectMember = new ProjectMember();
    projectMember.setAccount(account);
    projectMember.setProject(project);
    projectMember.setPermissions(permission);

    return projectMemberJPA.save(projectMember);
  }


}
