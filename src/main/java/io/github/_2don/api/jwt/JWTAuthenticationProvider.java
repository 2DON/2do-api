package io.github._2don.api.jwt;

import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    var account = accountJPA.findByEmail(email).orElseThrow(Status.NOT_FOUND);

    if (account.getVerificationSentAt() != null) {
      // account is not yet verified
      throw Status.LOCKED.get();
    }

    if (account.getDeleteRequest() != null) {
      if (account.getDeleteRequest().compareTo(new Date(System.currentTimeMillis())) > 0) {
        // account is still valid, so we will cancel the delete request
        accountJPA.save(account.setDeleteRequest(null));
      } else {
        throw Status.NOT_FOUND.get();
      }
    }

    if (!bcrypt.matches(password, account.getPassword())) {
      throw Status.UNAUTHORIZED.get();
    }

    return new UsernamePasswordAuthenticationToken(account.getId(), null, Collections.emptyList());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
