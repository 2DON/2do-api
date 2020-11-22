package io.github._2don.api.utils;

import io.github._2don.api.Application;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Resource {

  public static String readAsString(String absolutePath) {
    Optional<String> string = Optional.empty();

    try {
      string = Optional.of(FileCopyUtils
        .copyToString(new InputStreamReader(
          Application.class.getResourceAsStream("/templates/verification-mail.html"),
          StandardCharsets.UTF_8)));
    } catch (IOException ignored) {
    }

    return string.orElseThrow(Status.INTERNAL_SERVER_ERROR);
  }

}
