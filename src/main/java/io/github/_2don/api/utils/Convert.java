package io.github._2don.api.utils;

import java.util.Optional;

public class Convert {

  public static Optional<Long> toLong(String string) {
    try {
      return Optional.of(Long.parseLong(string, 10));
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

}
