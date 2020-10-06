package io.github._2don.api.security;

import java.sql.Date;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github._2don.api.repositories.AccountJPA;

public class JWTAuthenticationProvider implements AuthenticationProvider {

  public @Autowired BCryptPasswordEncoder bcrypt;
  public @Autowired AccountJPA accountJPA;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    var email = authentication.getName();
    var password = authentication.getCredentials().toString();

    var account =
        accountJPA.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

    if (account.getDeleteRequest() != null) {
      if (account.getDeleteRequest().compareTo(new Date(System.currentTimeMillis())) > 0) {
        // account is still valid, so we will cancel the delete request
        var stored = accountJPA.findById(account.getId()).orElseGet(() -> null);
        stored.setDeleteRequest(null);
        accountJPA.save(stored);
      } else {
        throw new UsernameNotFoundException("deleted");
      }
    }

    if (!bcrypt.matches(password, account.getPassword())) {
      throw new BadCredentialsException(email);
    }

    return new UsernamePasswordAuthenticationToken(account.getId(), null, Collections.emptyList());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
