package io.github._2don.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github._2don.api.repositories.AccountJPA;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private final AccountJPA accountJPA;
  private final JWTConfig jwtConfig;

  public JWTAuthorizationFilter(JWTConfig jwtConfig,
                                AccountJPA accountJPA,
                                AuthenticationManager authman) {
    super(authman);
    this.accountJPA = accountJPA;
    this.jwtConfig = jwtConfig;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws IOException, ServletException {

    var header = request.getHeader(jwtConfig.getTokenHeader());

    if (header == null || !header.startsWith(jwtConfig.getTokenPrefix())) {
      chain.doFilter(request, response);
      return;
    }

    var token = header.substring(jwtConfig.getTokenPrefix().length());

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
      response.setHeader("Access-Control-Expose-Headers", jwtConfig.getTokenHeader());
      response.setHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenExpiredValue());
      chain.doFilter(request, response);
      return;
    }

    // successful authentication, sets the @AuthenticationPrincipal principal to Long accountId
    SecurityContextHolder.getContext().setAuthentication(
      new UsernamePasswordAuthenticationToken(accountId, null, Collections.emptyList()));
    chain.doFilter(request, response);
  }

}
