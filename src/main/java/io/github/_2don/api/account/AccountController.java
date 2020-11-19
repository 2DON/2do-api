package io.github._2don.api.account;

import io.github._2don.api.jwt.JWTConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  @Autowired
  private JWTConfig jwtConfig;
  @Autowired
  private AccountService accountService;
  @Autowired
  private AccountJPA accountJPA;

  @GetMapping("/exists/{email}")
  public boolean exists(@PathVariable String email) {
    return accountJPA.existsByEmail(email);
  }

  @GetMapping("/me")
  public ResponseEntity<Account> info(@AuthenticationPrincipal Long accountId) {
    return ResponseEntity.of(accountJPA.findById(accountId));
  }

  @GetMapping
  public List<PublicAccount> list(@RequestParam List<Long> ids) {
    return accountJPA.findAllPublicByIdIn(ids);
  }

  @PatchMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public Account edit(@AuthenticationPrincipal Long accountId,
                      @RequestPart(name = "email", required = false) String email,
                      @RequestPart(name = "password", required = false) String password,
                      @RequestPart(name = "name", required = false) String name,
                      @RequestPart(name = "options", required = false) String options) throws ResponseStatusException {
    return accountService.update(accountId, email, password, name, options);
  }

  @PutMapping("/me/avatar")
  public Account editAvatar(@AuthenticationPrincipal Long accountId,
                            @RequestPart(name = "avatar", required = false) MultipartFile avatar) throws ResponseStatusException {
    return accountService.updateAvatar(accountId, avatar);
  }

  @DeleteMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public void destroy(@AuthenticationPrincipal Long accountId,
                      @RequestPart(name = "password") String password,
                      HttpServletResponse response) throws ResponseStatusException {
    // FIXME how to delete account
    accountService.delete(accountId, password);
    response.addHeader(jwtConfig.getTokenHeader(), jwtConfig.getTokenExpiredValue());
  }

  // TODO -- test only
  @GetMapping("/me/mock-premium")
  @ResponseStatus(HttpStatus.OK)
  public void premium(@AuthenticationPrincipal Long accountId) {
    accountService.obtainPremium(accountId);
  }

}
