package io.github._2don.api.utils;

import io.github._2don.api.projectmember.ProjectMemberPermission;

import java.util.Optional;

public class Convert {

  public static Optional<Long> toLong(String string) {
    try {
      return Optional.of(Long.parseLong(string, 10));
    } catch (NumberFormatException ignored) {
      return Optional.empty();
    }
  }

  public static Optional<ProjectMemberPermission> toProjectMemberPermission(String permission) {
    try {
      return Optional.of(ProjectMemberPermission.valueOf(permission));
    } catch (IllegalArgumentException | NullPointerException ignored) {
      return Optional.empty();
    }
  }
}
