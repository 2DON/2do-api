package io.github._2don.api.jwt;

import io.github._2don.api.account.AccountJPA;
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

    var accountId = JWTUtils.verify(token, jwtConfig.getSecret());

    if (accountId.isEmpty() || !accountJPA.existsById(accountId.get())) {
      response.setHeader("Access-Control-Expose-Headers", jwtConfig.getTokenHeader());
      response.setHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenExpiredValue());
      chain.doFilter(request, response);
      return;
    }

    // successful authentication, sets the @AuthenticationPrincipal principal to Long accountId
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
      accountId.get(),
      null,
      Collections.emptyList()));
    chain.doFilter(request, response);
  }

}
