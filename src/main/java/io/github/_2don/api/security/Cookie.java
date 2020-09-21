package io.github._2don.api.security;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder(builderMethodName = "")
public class Cookie {

  private @NonNull String name;
  private @NonNull String value;

  private @Builder.Default Integer maxAge = 0;
  private @Builder.Default boolean secure = false;
  private @Builder.Default boolean httpOnly = false;
  private @Builder.Default String domain = null;
  private @Builder.Default String path = null;
  private @Builder.Default SameSite sameSite = null;

  public static CookieBuilder createWith(@NotBlank String name, @NonNull String value) {
    return new CookieBuilder().name(name).value(value);
  }

  public static CookieBuilder delete(@NotBlank String name) {
    return new CookieBuilder().name(name).value("").maxAge(-1);
  }

  enum SameSite {
    NONE, STRICT, LAX;

    @Override
    public String toString() {
      return name().charAt(0) + name().substring(1).toLowerCase();
    }
  }

  @Override
  public String toString() {
    var cookie = new StringBuilder(name).append("=\"").append(value).append('"');

    if (maxAge != null) {
      cookie.append("; Max-Age=").append(maxAge);
    }

    if (secure) {
      cookie.append("; Secure");
    }

    if (httpOnly) {
      cookie.append("; HttpOnly");
    }

    if (domain != null) {
      cookie.append("; Domain=").append(domain);
    }

    if (path != null) {
      cookie.append("; Path=").append(path);
    }

    if (sameSite != null) {
      cookie.append("; SameSite=").append(sameSite.toString());
    }

    return cookie.toString();
  }

  public static class CookieBuilder {
    public String build() {
      return new Cookie(name, value, maxAge$value, secure$value, httpOnly$value, domain$value,
          path$value, sameSite$value).toString();
    }
  }
}
