package io.github._2don.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Collections;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JWTConfig jwtConfig;
  private final AuthenticationManager authman;

  public JWTAuthenticationFilter(JWTConfig jwtConfig, AuthenticationManager authman,
                                 String filterProcessesUrl) {
    this.jwtConfig = jwtConfig;
    this.authman = authman;
    setFilterProcessesUrl(filterProcessesUrl);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) {
    // get account from json response
    Credentials credentials;
    try {
      credentials = new ObjectMapper().readValue(request.getInputStream(), Credentials.class);
    } catch (IOException exception) {
      throw new UnknownError(exception.getLocalizedMessage());
    }

    if (credentials.getEmail() == null || credentials.getEmail().isEmpty()
      || credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
      throw new BadCredentialsException("invalid credentials");
    }

    // use the credentials to attemp authentication
    try {
      return authman.authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmail(),
        credentials.getPassword(), Collections.emptyList()));
    } catch (UsernameNotFoundException exception) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
    } catch (BadCredentialsException exception) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
    }

    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          FilterChain chain, Authentication auth) throws IOException, ServletException {

    var accountId = (Long) auth.getPrincipal();

    var token = JWT
      // create a token builder
      .create()
      // add the sub field
      .withSubject(Long.toString(accountId))
      // set expiration
      .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
      // sign the token using the hmac512 algorithm
      .sign(Algorithm.HMAC512(jwtConfig.getSecret()));

    response.setHeader("Access-Control-Expose-Headers", jwtConfig.getTokenHeader());
    response.setHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenPrefix() + token);
  }

}
