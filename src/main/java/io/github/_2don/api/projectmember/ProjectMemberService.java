package io.github._2don.api.projectmember;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.team.TeamJPA;
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
  private AccountJPA accountJPA;
  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private TeamJPA teamJPA;
  @Autowired
  private AccountService accountService;

  /**
   * Asserts if the project has reached the member limit.
   *
   * @param projectId projectId
   */
  public void assertMemberLimit(@NonNull Long projectId) {
    var owner = getOwner(projectId);
    if (owner.isPresent()
      && !owner.get().getPremium()
      && projectMemberJPA.countByProjectId(projectId) > NON_PREMIUM_MEMBER_LIMIT) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }
  }

  /**
   * Asserts if the user has reached the project limit
   *
   * @param accountId accountId
   */
  public void assertProjectLimit(@NonNull Long accountId) {
    var account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    assertProjectLimit(account);
  }

  /**
   * Asserts if the user has reached the project limit
   *
   * @param account account
   */
  public void assertProjectLimit(@NonNull Account account) {
    if (!account.getPremium()
      && projectMemberJPA.countByAccountIdAndPermissions(account.getId(), ProjectMemberPermissions.OWNER.ordinal()) >= NON_PREMIUM_PROJECT_LIMIT) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }
  }

  /**
   * Asserts if the user has reached the participation limit
   *
   * @param accountId accountId
   */
  public void assertParticipationLimit(@NonNull Long accountId) {
    var account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!account.getPremium()
      && projectMemberJPA.countByAccountId(accountId) >= NON_PREMIUM_PARTICIPATION_LIMIT) {
      throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED);
    }
  }

  /**
   * Assert if member is part of project
   *
   * @param accountId accountId
   * @param projectId projectId
   */
  public void assertIsMember(@NonNull Long accountId,
                             @NonNull Long projectId) {
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  /**
   * @param accountId accountId
   * @param projectId projectId
   * @return list of members for the project
   */
  @NonNull
  public List<ProjectMemberDTO> list(@NonNull Long accountId,
                                     @NonNull Long projectId) {
    assertIsMember(accountId, projectId);

    return projectMemberJPA.findAllByProjectId(projectId);
  }

  /**
   * @param accountId accountId
   * @param projectId projectId
   * @return ProjectMember
   */
  @NonNull
  private ProjectMember getMeta(Long accountId, Long projectId) {
    return projectMemberJPA
      .findByAccountIdAndProjectId(accountId, projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  /**
   * @param projectId projectId
   * @return the owner of the project
   */
  private Optional<Account> getOwner(@NonNull Long projectId) {
    var meta = projectMemberJPA
      .findByProjectIdAndPermissions(projectId, ProjectMemberPermissions.OWNER.ordinal());

    return meta.map(ProjectMember::getAccount);
  }

  /**
   * @param loggedId    user that's requesting the addition
   * @param projectId   projectId
   * @param accountId   accountId
   * @param teamId      teamId - nullable
   * @param permissions permissions
   */
  public ProjectMemberDTO add(@NonNull Long loggedId,
                              @NonNull Long projectId,
                              @NonNull Long accountId,
                              Long teamId,
                              @NonNull ProjectMemberPermissions permissions) {

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
      .setAccount(accountJPA.getOne(accountId))
      .setProject(loggedMeta.getProject())
      .setPermissions(permissions)
      .setCreatedBy(loggedMeta.getAccount())
      .setUpdatedBy(loggedMeta.getAccount());

    if (teamId != null && teamJPA.existsById(teamId)) {
      projectMember.setTeam(teamJPA.getOne(teamId));
    }

    return ProjectMemberDTO.from(projectMemberJPA.save(projectMember));
  }

  public ProjectMemberDTO edit(@NonNull Long loggedId,
                               @NonNull Long projectId,
                               @NonNull Long accountId,
                               Long teamId,
                               ProjectMemberPermissions permissions) {

    var loggedMeta = getMeta(loggedId, projectId);
    var loggedPerm = loggedMeta.getPermissions();
    var accountMeta = getMeta(accountId, projectId);
    var accountPerm = accountMeta.getPermissions();

    if (permissions != null) {
      if (loggedPerm.compareTo(ProjectMemberPermissions.MAN_MEMBERS) < 0
        || loggedPerm.compareTo(accountPerm) < 0
        || loggedPerm.compareTo(permissions) < 0) {
        // not a member_manager+ or trying to apply permissions higher then himself
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      accountMeta.setPermissions(permissions);
    }

    if (teamId != null && teamJPA.existsById(teamId)) {
      accountMeta.setTeam(teamJPA.getOne(teamId));
    }

    accountMeta.setUpdatedBy(loggedMeta.getAccount());

    return ProjectMemberDTO.from(projectMemberJPA.save(accountMeta));
  }

  public void delete(@NonNull Long loggedId,
                     @NonNull Long projectId,
                     @NonNull Long accountId) {
    var loggedMeta = getMeta(loggedId, projectId);
    var loggedPerm = loggedMeta.getPermissions();
    var accountMeta = getMeta(accountId, projectId);
    var accountPerm = accountMeta.getPermissions();

    if (loggedPerm.compareTo(ProjectMemberPermissions.MAN_MEMBERS) < 0
      || loggedPerm.compareTo(accountPerm) < 0) {
      // not a member_manager+ or trying to apply permissions higher then himself
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    projectMemberJPA.delete(accountMeta);
  }

  public void transferOwnership(Long ownerId, Long projectId, Long newOwnerId) {
    var owner = projectMemberJPA
      .findByAccountIdAndProjectIdAndPermissions(ownerId, projectId, ProjectMemberPermissions.OWNER.ordinal())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    var newOwner = getMeta(newOwnerId, projectId);

    assertProjectLimit(newOwner.getAccount());

    owner
      .setPermissions(ProjectMemberPermissions.MAN_PROJECT)
      .setUpdatedBy(owner.getAccount());

    newOwner
      .setPermissions(ProjectMemberPermissions.OWNER)
      .setUpdatedBy(owner.getAccount());

    projectMemberJPA.save(owner);
    projectMemberJPA.save(newOwner);
  }

}
