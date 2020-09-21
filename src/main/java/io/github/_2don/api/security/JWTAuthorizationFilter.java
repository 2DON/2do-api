package io.github._2don.api.security;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.github._2don.api.repositories.AccountJPA;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private AccountJPA accountJPA;
  private JWTConfig jwtConfig;

  public JWTAuthorizationFilter(JWTConfig jwtConfig, AccountJPA accountJPA,
      AuthenticationManager authman) {
    super(authman);
    this.accountJPA = accountJPA;
    this.jwtConfig = jwtConfig;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    var cookies = request.getCookies();
    if (cookies == null) {
      chain.doFilter(request, response);
      return;
    }

    var cookie =
        Stream.of(cookies).filter(c -> jwtConfig.getCookieName().equals(c.getName())).findFirst();
    if (!cookie.isPresent() || !cookie.get().getValue().startsWith(jwtConfig.getTokenPrefix())) {
      chain.doFilter(request, response);
      return;
    }

    var token = cookie.get().getValue().substring(jwtConfig.getTokenPrefix().length());

    Long accountId;
    try {
      accountId = Long.valueOf(
          // create a token verifier with the same algorithm as before
          JWT.require(Algorithm.HMAC512(jwtConfig.getSecret())).build()
              // verify the token
              .verify(token)
              // get the accountId
              .getSubject());
    } catch (JWTVerificationException e) {
      accountId = null;
    }

    if (accountId == null || !accountJPA.existsById(accountId)) {
      response.addHeader("Set-Cookie", Cookie.delete(jwtConfig.getCookieName()).path("/").build());
      chain.doFilter(request, response);
      return;
    }

    // successful authentication, sets the @AuthenticationPrincipal principal to Long accountId
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(accountId, null, Collections.emptyList()));
    chain.doFilter(request, response);
  }

}
