package io.github._2don.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.models.Account;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.utils.Patterns;

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
  public void signUp(@RequestBody Account account) {
    if (account.getEmail() == null || !Patterns.EMAIL.matches(account.getEmail())
        || account.getPassword() == null || account.getPassword().length() < 8) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    if (accountJPA.existsByEmail(account.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    account.setPassword(bcrypt.encode(account.getPassword()));
    account.setName(account.getEmail());
    account.setAvatar(null);
    account.setOptions(null);

    accountJPA.save(account);
  }

  @PatchMapping("/edit")
  @ResponseStatus(HttpStatus.OK)
  public Account edit(@AuthenticationPrincipal Long accountId, @RequestBody Account account) {
    var stored = accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (account.getEmail() != null) {
      if (!Patterns.EMAIL.matches(account.getEmail())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      if (accountJPA.existsByEmail(account.getEmail())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
      }

      stored.setEmail(account.getEmail());
    }

    if (account.getPassword() != null) {
      if (account.getPassword().length() < 8) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      stored.setPassword(bcrypt.encode(account.getPassword()));
    }

    if (account.getName() != null) {
      stored.setName(account.getName());
    }

    if (account.getOptions() != null) {
      stored.setOptions(account.getOptions());
    }

    // TODO: recieve image, check size, etc... BASE64? FILE?
    // if (account.getAvatar() != null) {
    // stored.setName(account.getName());
    // }

    return accountJPA.save(stored);
  }

  @GetMapping("/info")
  public Account info(@AuthenticationPrincipal Long accountId) {
    return accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

}
