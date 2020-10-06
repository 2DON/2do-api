package io.github._2don.api.controllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.models.Account;
import io.github._2don.api.projections.PublicAccount;
import io.github._2don.api.repositories.AccountJPA;
import io.github._2don.api.security.JWTConfig;
import io.github._2don.api.utils.Patterns;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private static final List<String> avatarTypes =
      List.of("image/png", "image/jpeg", "application/octet-stream");

  private @Autowired AccountJPA accountJPA;
  private @Autowired BCryptPasswordEncoder bcrypt;
  private @Autowired JWTConfig jwtConfig;

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
    account.setOptions(null);

    accountJPA.save(account);
  }

  @PatchMapping("/edit")
  @ResponseStatus(HttpStatus.OK)
  public Account edit(@AuthenticationPrincipal Long accountId,
      @RequestPart(name = "email", required = false) String email,
      @RequestPart(name = "password", required = false) String password,
      @RequestPart(name = "name", required = false) String name,
      @RequestPart(name = "options", required = false) String options,
      @RequestPart(name = "avatar", required = false) MultipartFile avatar) throws IOException {

    var stored = accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (email != null) {
      if (!Patterns.EMAIL.matches(email)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
      if (accountJPA.existsByEmail(email)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
      }

      stored.setEmail(email);
    }

    if (password != null) {
      if (password.length() < 8) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      stored.setPassword(bcrypt.encode(password));
    }

    if (name != null) {
      stored.setName(name);
    }

    if (options != null) {
      stored.setOptions(options);
    }

    if (avatar != null) {
      if (!avatarTypes.contains(avatar.getContentType())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      stored.setAvatarUrl(
          "data:image/png;base64," + Base64.getEncoder().encodeToString(avatar.getBytes()));
    }

    return accountJPA.save(stored);
  }

  @GetMapping("/info")
  public ResponseEntity<Account> info(@AuthenticationPrincipal Long accountId) {
    return ResponseEntity.of(accountJPA.findById(accountId));
  }


  @GetMapping("/info/{id}")
  public ResponseEntity<PublicAccount> show(@PathVariable("id") Long accountId) {
    return ResponseEntity.of(accountJPA.findPublicById(accountId));
  }

  @DeleteMapping("/delete")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @RequestBody String password,
      HttpServletResponse response) {
    var account = accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!bcrypt.matches(password, account.getPassword())) {
      // wrong password
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    account.setDeleteRequest(Date.valueOf(LocalDate.now().plusMonths(1)));
    accountJPA.save(account);
    response.addHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenExpiredValue());
  }
}
