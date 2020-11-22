package io.github._2don.api.utils;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

  public static long nowPlusMinutes(Long minutes) {
    return Instant.now().toEpochMilli() + TimeUnit.MINUTES.toMillis(minutes);
  }

}
