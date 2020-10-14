package io.github._2don.api.security;

import io.github._2don.api.repositories.AccountJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.Collections;

public class JWTAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private BCryptPasswordEncoder bcrypt;
  @Autowired
  private AccountJPA accountJPA;

  @Override
  public Authentication authenticate(Authentication authentication) {

    var email = authentication.getName();
    var password = authentication.getCredentials().toString();

    var account = accountJPA.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (account.getDeleteRequest() != null) {
      if (account.getDeleteRequest().compareTo(new Date(System.currentTimeMillis())) > 0) {
        // account is still valid, so we will cancel the delete request
        var stored = accountJPA.findById(account.getId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE));
        stored.setDeleteRequest(null);
        accountJPA.save(stored);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
    }

    if (!bcrypt.matches(password, account.getPassword())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    return new UsernamePasswordAuthenticationToken(account.getId(), null, Collections.emptyList());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
