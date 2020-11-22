package io.github._2don.api.projectmember;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.account.AccountService;
import io.github._2don.api.project.ProjectService;
import io.github._2don.api.team.TeamJPA;
import io.github._2don.api.team.TeamService;
import io.github._2don.api.teammember.TeamMemberService;
import io.github._2don.api.utils.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static io.github._2don.api.projectmember.ProjectMemberPermission.MAN_MEMBERS;
import static io.github._2don.api.projectmember.ProjectMemberPermission.OWNER;

@Service
public class ProjectMemberService {

  private final long NON_PREMIUM_OWN_PROJECT_MEMBER_LIMIT;
  private final long NON_PREMIUM_PARTICIPATIONS_LIMIT;

  @Autowired
  private ProjectMemberJPA projectMemberJPA;
  @Autowired
  private TeamService teamService;
  @Autowired
  private AccountService accountService;
  @Autowired
  private TeamMemberService teamMemberService;
  @Autowired
  private TeamJPA teamJPA;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private ProjectService projectService;

  public ProjectMemberService(@Value("${non-premium-limits.own-project-members}") long nonPremiumOwnProjectMemberLimit,
                              @Value("${non-premium-limits.participations}") long nonPremiumParticipationsLimit) {
    this.NON_PREMIUM_OWN_PROJECT_MEMBER_LIMIT = nonPremiumOwnProjectMemberLimit;
    this.NON_PREMIUM_PARTICIPATIONS_LIMIT = nonPremiumParticipationsLimit;
  }

  public void assertMemberLimit(@NonNull Long projectId) {
    var owner = findOwner(projectId);
    if (owner.isPresent() && !owner.get().getPremium()
      && projectMemberJPA.countByProjectId(projectId) >= NON_PREMIUM_OWN_PROJECT_MEMBER_LIMIT) {
      throw Status.UPGRADE_REQUIRED.get();
    }
  }

  public void assertParticipationLimit(@NonNull Long accountId) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);

    if (!account.getPremium()
      && projectMemberJPA.countByAccountId(accountId) >= NON_PREMIUM_PARTICIPATIONS_LIMIT) {
      throw Status.UPGRADE_REQUIRED.get();
    }
  }

  public void assertIsMember(@NonNull Long accountId, @NonNull Long projectId, HttpStatus status) {
    if (!projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw new ResponseStatusException(status);
    }
  }

  @NonNull
  public List<ProjectMemberDTO> findMembers(@NonNull Long accountId,
                                            @NonNull Long projectId) {
    assertIsMember(accountId, projectId, HttpStatus.UNAUTHORIZED);
    return projectMemberJPA.findAllByProjectId(projectId);
  }

  @NonNull
  public ProjectMember findIfIsMember(Long accountId, Long projectId) {
    return projectMemberJPA
      .findByAccountIdAndProjectIdAndProjectArchived(accountId, projectId, false)
      .orElseThrow(Status.UNAUTHORIZED);
  }

  @NonNull
  public ProjectMember findIfIsMemberAndHavePermission(@NonNull Long accountId,
                                                       @NonNull Long projectId,
                                                       @NonNull ProjectMemberPermission permission) {
    var member = findIfIsMember(accountId, projectId);

    if (member.getPermission().lessThan(permission)) {
      throw Status.UNAUTHORIZED.get();
    }

    return member;
  }

  @NonNull
  private Optional<Account> findOwner(@NonNull Long projectId) {
    return projectMemberJPA
      .findByProjectIdAndPermission(projectId, OWNER)
      .map(ProjectMember::getAccount);
  }

  @NonNull
  public ProjectMemberDTO addMember(@NonNull Long loggedId,
                                    @NonNull Long projectId,
                                    @NonNull Long accountId,
                                    Long teamId,
                                    @NonNull ProjectMemberPermission permission) {
    assertMemberLimit(projectId); // pass if: project is owned by a premium account or is below the NON_PREMIUM_MEMBER_LIMIT
    accountService.assertExists(accountId, HttpStatus.NOT_FOUND); // account exists, is not on the verification step or deleted
    assertParticipationLimit(accountId); // pass if: new member is a premium or is below the NON_PREMIUM_PARTICIPATION_LIMIT

    if (projectMemberJPA.existsByAccountIdAndProjectId(accountId, projectId)) {
      throw Status.CONFLICT.get();
    }

    var member = findIfIsMemberAndHavePermission(loggedId, projectId, MAN_MEMBERS);
    if (permission.greaterOrEqualTo(member.getPermission())) {
      throw Status.UNAUTHORIZED.get();
    }

    var newMember = new ProjectMember()
      .setAccountId(accountId)
      .setProjectId(projectId)
      .setPermission(permission)
      .setCreatedBy(member.getAccount())
      .setUpdatedBy(member.getAccount());

    if (teamId != null) {
      teamMemberService.assertIsMember(accountId, teamId);
      newMember.setTeam(teamJPA.getOne(teamId));
    }

    newMember = projectMemberJPA.save(newMember);

    return ProjectMemberDTO.from(newMember);
  }

  @NonNull
  public ProjectMemberDTO update(@NonNull Long loggedId,
                                 @NonNull Long projectId,
                                 @NonNull Long accountId,
                                 Long teamId,
                                 ProjectMemberPermission permissions) {
    var logged = findIfIsMemberAndHavePermission(loggedId, projectId, MAN_MEMBERS);
    var target = findIfIsMember(accountId, projectId);

    if (permissions != null) {
      if (target.getPermission().greaterOrEqualTo(logged.getPermission())
        || permissions.greaterOrEqualTo(logged.getPermission())) {
        throw Status.UNAUTHORIZED.get();
      }
      // perms < logged > target
      target.setPermission(permissions);
    }

    if (teamId != null) {
      teamMemberService.assertIsMember(accountId, teamId);
      target.setTeam(teamJPA.getOne(teamId));
    }

    target.setUpdatedBy(logged.getAccount());

    return ProjectMemberDTO.from(projectMemberJPA.save(target));
  }

  public void leaveOrRemoveMember(@NonNull Long loggedId,
                                  @NonNull Long projectId,
                                  @NonNull Long accountId) {
    var target = findIfIsMember(accountId, projectId);

    if (target.isOwner()) {
      // no-one can remove the owner, he needs to transfer ownership first
      throw Status.LOCKED.get();
    }

    if (!loggedId.equals(accountId)) {
      var logged = findIfIsMemberAndHavePermission(loggedId, projectId, MAN_MEMBERS);
      if (target.getPermission().greaterOrEqualTo(logged.getPermission())) {
        throw Status.UNAUTHORIZED.get();
      }
    }

    projectMemberJPA.delete(target);
  }

}
