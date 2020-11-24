package io.github._2don.api.utils;

import java.util.Optional;

public class Convert {

  public static Optional<Long> toLong(String raw) {
    try {
      return Optional.of(Long.parseLong(raw, 10));
    } catch (NumberFormatException ignored) {
      return Optional.empty();
    }
  }

  public static Optional<Integer> toInteger(String raw) {
    try {
      return Optional.of(Integer.parseInt(raw, 10));
    } catch (NumberFormatException ignored) {
      return Optional.empty();
    }
  }

  public static <T extends Enum<T>> Optional<T> toEnum(Class<T> type, String raw) {
    try {
      return Optional.of(T.valueOf(type, raw));
    } catch (IllegalArgumentException | NullPointerException ignored) {
      return Optional.empty();
    }
  }

  public static Optional<Boolean> toBoolean(String raw) {
    try {
      return Optional.of(Boolean.parseBoolean(raw));
    } catch (NumberFormatException ignored) {
      return Optional.empty();
    }
  }

}
