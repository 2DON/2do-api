package io.github._2don.api.auth.verify;

import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class VerificationService {

  @Autowired
  private AccountJPA accountJPA;

  public void sendMail(Account account) {

  }

  public String verify(String token, @NonNull Model model) {
    model.addAttribute("account_name", "Wesley");
    model.addAttribute("account_email", "wesauis@htb.local");

    return "verification-success";
  }

}
