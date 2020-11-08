package io.github._2don.api.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JWTConfig {

  @Value("${auth.secret}")
  @Getter(AccessLevel.PACKAGE)
  private String secret;

  @Value("${auth.token.header}")
  private String tokenHeader;

  @Value("${auth.token.prefix}")
  private String tokenPrefix;

  @Value("${auth.token.expiration}")
  private long expiration;

  @Value("${auth.token.expired_value}")
  private String tokenExpiredValue;

}
