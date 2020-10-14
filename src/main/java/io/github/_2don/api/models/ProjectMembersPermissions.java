package io.github._2don.api.models;

public enum ProjectMembersPermissions {
  /**
   * Can see the project, tasks and steps
   */
  VIEW,
  /**
   * Can add, modify, complete and remote tasks and steps
   */
  MODIFY,
  /**
   * Can invite users to view the project
   */
  INVITE,
  /**
   * Can remove and promote participants, and demote participants with lower permissions
   */
  MODERATE,
  /**
   * Can manage the project, allowing everything except deleting the project
   */
  MANAGE,
  /**
   * Allowed to do everything, including deleting and archiving the project
   */
  ALL;
}
