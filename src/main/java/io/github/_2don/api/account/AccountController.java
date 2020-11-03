package io.github._2don.api.account;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import io.github._2don.api.jwt.JWTConfig;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  @Autowired
  private JWTConfig jwtConfig;
  @Autowired
  private AccountService accountService;

  @GetMapping("/exists/{email}")
  public boolean exists(@PathVariable String email) {
    return this.accountService.exist(email);
  }

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public Account signUp(@RequestBody Account account) {
    return this.accountService.add(account);
  }

  @PatchMapping("/edit")
  @ResponseStatus(HttpStatus.OK)
  public Account edit(@AuthenticationPrincipal Long accountId,
      @RequestPart(name = "email", required = false) String email,
      @RequestPart(name = "password", required = false) String password,
      @RequestPart(name = "name", required = false) String name,
      @RequestPart(name = "options", required = false) String options,
      @RequestPart(name = "removeAvatar", required = false) String removeAvatar,
      @RequestPart(name = "avatar", required = false) MultipartFile avatar) throws Exception {
    return this.accountService.update(accountId, email, password, name, options, avatar);
  }

  @GetMapping("/info")
  public ResponseEntity<Account> info(@AuthenticationPrincipal Long accountId) {
    return ResponseEntity.ok(this.accountService.info(accountId));
  }

  @GetMapping("/info/{accountId}")
  public ResponseEntity<PublicAccount> show(@PathVariable Long accountId) {
    return ResponseEntity.ok(this.accountService.infoPublic(accountId));
  }

  @DeleteMapping("/delete")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId, @RequestBody String password,
      HttpServletResponse response) {

    this.accountService.delete(accountId, password);
    response.addHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenExpiredValue());
  }

  @PostMapping("/premium")
  @ResponseStatus(HttpStatus.OK)
  public void premium(@AuthenticationPrincipal Long accountId) {
    this.accountService.obtainPremium(accountId);
  }
}
