package io.github._2don.api.auth.verify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth/sign-up/verify")
public class VerificationController {

  @Autowired
  private VerificationService verificationService;

  @GetMapping()
  public String verify(@RequestParam(required = false) String token, Model model) {
    return verificationService.verify(token, model);
  }

}
