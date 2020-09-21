package io.github._2don.api.services;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.security.AccountDetails;

@Service
public class AccountDetailsService implements UserDetailsService {

  private @Autowired AccountJPA accountJPA;

  @Override
  public UserDetails loadUserByUsername(String username) {
    var account = accountJPA.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    return new AccountDetails(account.getId(), account.getUsername(), account.getPassword(),
        Collections.emptyList());
  }

}
