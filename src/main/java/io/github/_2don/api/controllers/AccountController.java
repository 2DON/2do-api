package io.github._2don.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private @Autowired AccountJPA accountJPA;
  private @Autowired BCryptPasswordEncoder bcrypt;

  @GetMapping("/exists/{email}")
  public boolean exists(@PathVariable("email") String email) {
    return accountJPA.existsByEmail(email);
  }

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@Validated @RequestBody Account account) {
    account.setPassword(bcrypt.encode(account.getPassword()));

    if (accountJPA.existsByEmail(account.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    accountJPA.save(account);
  }

  @GetMapping("/info")
  public Account info(@AuthenticationPrincipal Long accountId) {
    return accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

}
