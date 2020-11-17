package io.github._2don.api.utils;

import java.util.Base64;
import java.util.Set;

public class ImageEncoder {

  private static final String BASE64_ENCODED_IMAGE_PREFIX = "data:image/png;base64,";
  private static final Set<String> MIME_TYPES =
    Set.of("image/png", "image/jpeg", "application/octet-stream");


  private ImageEncoder() {
  }

  public static boolean supports(String mimeType) {
    return MIME_TYPES.contains(mimeType);
  }

  public static String encodeToString(byte[] bytes) {
    return BASE64_ENCODED_IMAGE_PREFIX + Base64.getEncoder().encodeToString(bytes);
  }

}
