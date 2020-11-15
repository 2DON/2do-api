package io.github._2don.api.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.sql.Date;
import java.util.Optional;

public class JWTUtils {

  public static String create(Long accountId, Long expiration, String secret) {
    return JWT
      .create()
      .withSubject(Long.toString(accountId))
      .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
      .sign(Algorithm.HMAC512(secret));
  }

  public static Optional<Long> verify(String token, String secret) {
    Long sub;
    try {
      sub = Long.valueOf(JWT
        .require(Algorithm.HMAC512(secret)).build()
        .verify(token)
        .getSubject());
    } catch (JWTVerificationException ignored) {
      sub = null;
    }
    return Optional.ofNullable(sub);
  }

}
