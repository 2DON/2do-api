package io.github._2don.api.auth.verify;

import com.sendgrid.Content;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.jwt.JWTUtils;
import io.github._2don.api.mail.EmailService;
import io.github._2don.api.utils.Resource;
import io.github._2don.api.utils.TimeUtils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class VerificationService {

  private final String SECRET;
  private final long MIN_EXPIRATION;
  private final long MAX_EXPIRATION;

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private EmailService emailService;

  public VerificationService(@Value("${auth.verification.secret:${random.value}}") String secret,
                             @Value("${auth.verification.min_exp:5}") long minExp,
                             @Value("${auth.verification.max_exp:10}") long maxExp) {
    SECRET = secret;
    MIN_EXPIRATION = minExp;
    MAX_EXPIRATION = maxExp;
  }

  /**
   * Asserts if canAfter >= now.
   *
   * @param canAfter timestamp that indicates when the token can be re-generated
   * @throws ResponseStatusException
   */
  private void assertCanRegenTokenNow(@NonNull Timestamp canAfter) throws ResponseStatusException {
    if (canAfter.toInstant().toEpochMilli() >= Instant.now().toEpochMilli()) {
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }
  }

  @NonNull
  private String createVerificationToken(@NonNull Account account) {
    var verification = Optional
      .ofNullable(account.getVerification())
      .orElse(new AccountVerification()
        .setId(account.getId())
        .setAccount(account)
        .setMinExp(new Timestamp(TimeUtils
          .nowPlusMinutes(MIN_EXPIRATION))));

    if (verification.getToken() != null) {
      assertCanRegenTokenNow(verification.getMinExp());
    }

    var token = Optional
      .ofNullable(verification.getToken())
      .orElse(JWTUtils.create(
        account.getId(),
        MAX_EXPIRATION,
        SECRET));

    account.setVerification(verification.setToken(token));
    accountJPA.save(account);
    return token;
  }

  public void sendMail(@NonNull Account account) throws ResponseStatusException, IOException {
    var token = createVerificationToken(account);

    var html = Resource.readAsString("/templates/verification-mail.html");
    html = html.replaceAll("\\{\\{account_name}}", account.getName());
    html = html.replaceAll("\\{\\{account_verification_url}}", "https://google.com");

    emailService.sendEmail("email", "2DO verification mail", new Content("text/html", html));
  }

  public String verify(String token, @NonNull Model model) {
    model.addAttribute("account_name", "Wesley");
    model.addAttribute("account_email", "wesauis@htb.local");

    return "verification-success";
  }

}
