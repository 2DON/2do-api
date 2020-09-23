package io.github._2don.api.security;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import io.github._2don.api.security.Cookie.SameSite;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private JWTConfig jwtConfig;
  private AuthenticationManager authman;

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

    var cookie = Cookie
        // create the token builder with name and value
        .createWith(jwtConfig.getCookieName(), jwtConfig.getTokenPrefix() + token)
        // HttpOnly: removes js acess to the cookie
        .httpOnly(true)
        // Firefox and Chrome accept SameSite=Strict attribute, in theory, avoid CSRF
        .sameSite(SameSite.STRICT)
        // when Max-Age is set the token will not be deleted on session end
        .maxAge(jwtConfig.getMaxAge())
        // send to all domain paths
        .path("/")
        // only send the cookie over HTTPS / disable this in production mode setting auth.secure to
        // false
        .secure(jwtConfig.isSecure()).build();

    response.addHeader("Set-Cookie", cookie);
  }

}
