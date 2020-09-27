package io.github._2don.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@Configuration
public class JWTConfig {

  @Value("${auth.secret}")
  private @Getter(AccessLevel.PACKAGE) String secret;

  @Value("${auth.token.header}")
  private String tokenHeader;

  @Value("${auth.token.prefix}")
  private String tokenPrefix;

  @Value("${auth.token.expiration}")
  private long expiration;

  @Value("${auth.token.expired_value}")
  private String tokenExpiredValue;

}
