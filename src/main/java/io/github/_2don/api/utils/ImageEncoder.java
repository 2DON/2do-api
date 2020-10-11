package io.github._2don.api.utils;

import java.util.Base64;
import java.util.Set;

public class ImageEncoder {

  public static final Set<String> MIME_TYPES =
    Set.of("image/png", "image/jpeg", "application/octet-stream");

  private static final String BASE64_ENCODED_IMAGE_PREFIX = "data:image/png;base64,";

  private ImageEncoder() {
  }

  public static String encodeToString(byte[] bytes) {
    return BASE64_ENCODED_IMAGE_PREFIX + Base64.getEncoder().encodeToString(bytes);
  }

}
