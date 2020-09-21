package io.github._2don.api.controllers;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.models.Account;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.security.Cookie;
import io.github._2don.api.security.JWTConfig;

@RestController
@RequestMapping("/accounts")
public class AccountController {
  private static final String USERNAME_CONFLICT = "'%s' is already in use";

  private @Autowired JWTConfig jwtConfig;
  private @Autowired AccountJPA accountJPA;
  private @Autowired BCryptPasswordEncoder bcrypt;

  @GetMapping("/exists/{username}")
  public boolean exists(@PathVariable("username") String username) {
    return accountJPA.existsByUsername(username);
  }

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@Validated @RequestBody Account account) {
    account.setPassword(bcrypt.encode(account.getPassword()));

    if (accountJPA.existsByUsername(account.getUsername())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(USERNAME_CONFLICT, account.getUsername()));
    }

    accountJPA.save(account);
  }

  @GetMapping("/sign-out")
  public void signOut(HttpServletResponse response) {
    response.addHeader("Set-Cookie", Cookie.delete(jwtConfig.getCookieName()).path("/").build());
  }

}
