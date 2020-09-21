package io.github._2don.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.AccessLevel;

@Getter
@Configuration
public class JWTConfig {
  private final String cookieName;
  private final String tokenPrefix;
  private final @Getter(AccessLevel.PACKAGE) String secret;
  private final long expiration;
  private final int maxAge;
  private final boolean secure;

  public JWTConfig(@Value("${auth.secret}") String secret,
      @Value("${auth.expiration}") int expiration, @Value("${auth.secure:false}") boolean secure) {

    this.secure = secure;
    var cookieNamePrefix = secure ? "__Host-" : "";
    this.cookieName = cookieNamePrefix + "Authorization";
    this.tokenPrefix = "Bearer_";
    this.secret = secret;
    this.expiration = expiration * 1000L;
    this.maxAge = expiration;
  }
}
