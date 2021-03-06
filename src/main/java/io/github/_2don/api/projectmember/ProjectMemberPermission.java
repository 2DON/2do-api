package io.github._2don.api.projectmember;

public enum ProjectMemberPermission {
  /**
   * Can see the project, tasks and steps
   */
  VIEW,
  /**
   * Can add, modify, complete and remove tasks and steps
   */
  MAN_TASKS,
  /**
   * Can invite, remove, promote and demote participants, lower permissions
   */
  MAN_MEMBERS,
  /**
   * Can manage the project, allowing everything except deleting the project
   */
  MAN_PROJECT,
  /**
   * Allowed to do everything, including deleting and archive the project
   */
  OWNER;

  public boolean lessThan(ProjectMemberPermission permission) {
    return this.ordinal() < permission.ordinal();
  }

  public boolean greaterOrEqualTo(ProjectMemberPermission permission) {
    return this.ordinal() >= permission.ordinal();
  }
}
