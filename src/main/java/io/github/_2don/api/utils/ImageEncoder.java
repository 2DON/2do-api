package io.github._2don.api.utils;

import java.util.Base64;

public class ImageEncoder {

  private static final String BASE64_ENCODED_IMAGE_PREFIX = "data:image/png;base64,";

  private ImageEncoder() {
  }

  public static String encodeToString(byte[] bytes) {
    return BASE64_ENCODED_IMAGE_PREFIX + Base64.getEncoder().encodeToString(bytes);
  }

}
