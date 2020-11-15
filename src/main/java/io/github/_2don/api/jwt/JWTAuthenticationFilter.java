package io.github._2don.api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github._2don.api.account.Credentials;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JWTConfig jwtConfig;
  private final AuthenticationManager authman;

  public JWTAuthenticationFilter(JWTConfig jwtConfig,
                                 AuthenticationManager authman,
                                 String filterProcessesUrl) {
    this.jwtConfig = jwtConfig;
    this.authman = authman;
    setFilterProcessesUrl(filterProcessesUrl);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) {
    // get account from json response
    Credentials.Impl credentials;
    try {
      credentials = new ObjectMapper().readValue(request.getInputStream(), Credentials.Impl.class);
    } catch (IOException exception) {
      throw new UnknownError(exception.getLocalizedMessage());
    }

    if (credentials.getEmail() == null || credentials.getEmail().isEmpty()
      || credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
      throw new BadCredentialsException("invalid credentials");
    }

    // use the credentials to attempt authentication
    try {
      return authman.authenticate(new UsernamePasswordAuthenticationToken(
        credentials.getEmail(),
        credentials.getPassword(),
        Collections.emptyList()));
    } catch (ResponseStatusException exception) {
      response.setStatus(exception.getStatus().value());
    }

    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication auth) throws IOException, ServletException {
    var token = JWTUtils.create(
      (Long) auth.getPrincipal(),
      jwtConfig.getExpiration(),
      jwtConfig.getSecret());

    response.setHeader("Access-Control-Expose-Headers", jwtConfig.getTokenHeader());
    response.setHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenPrefix() + token);
  }

}
