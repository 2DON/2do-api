package io.github._2don.api.auth;

import io.github._2don.api.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AccountService accountService;

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@RequestPart(name = "email") String email,
                     @RequestPart(name = "password") String password,
                     @RequestPart(name = "name", required = false) String name,
                     @RequestPart(name = "options", required = false) String options,
                     @RequestPart(name = "avatar") MultipartFile avatar) throws IOException {
    accountService.create(email, password, name, options, avatar);
  }

  @PostMapping("/sign-up/fix-email")
  public void fixEmail(@RequestParam String email,
                       @RequestPart(name = "new-email") String newEmail) throws IOException {
    accountService.fixEmail(email, newEmail);
  }

}
