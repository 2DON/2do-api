package io.github._2don.api.auth.verify;

import com.sendgrid.Content;
import io.github._2don.api.account.Account;
import io.github._2don.api.account.AccountJPA;
import io.github._2don.api.jwt.JWTUtils;
import io.github._2don.api.mail.EmailService;
import io.github._2don.api.utils.Resource;
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
import java.util.concurrent.TimeUnit;

@Service
public class VerificationService {

  private static final String VERIFICATION_HTML_TEMPLATE
    = Resource.readAsString("/templates/verification-mail.html");
  private static final String VERIFICATION_ENDPOINT_TEMPLATE
    = "%s:%s/auth/sign-up/verify?token=%s";

  private final String SERVER_HOST;
  private final int SERVER_PORT;
  private final String SECRET;
  private final String SUBJECT;
  private final long MIN_EXPIRATION;
  private final long MAX_EXPIRATION;


  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private EmailService emailService;

  public VerificationService(@Value("${server.host:http://127.0.0.1}") String serverHost,
                             @Value("${server.port:8080}") int serverPort,
                             @Value("${auth.verification.secret:${random.value}}") String secret,
                             @Value("${auth.verification.subject}") String subject,
                             @Value("${auth.verification.min_exp:5}") long minExp,
                             @Value("${auth.verification.max_exp:10}") long maxExp) {
    SERVER_HOST = serverHost;
    SERVER_PORT = serverPort;
    SECRET = secret;
    SUBJECT = subject;
    MIN_EXPIRATION = minExp;
    MAX_EXPIRATION = maxExp;
  }

  @NonNull
  private static Content buildMailContent(@NonNull String name,
                                          @NonNull String url) {
    return new Content(
      "text/html",
      VERIFICATION_HTML_TEMPLATE
        .replaceAll("\\{\\{account_name}}", name)
        .replaceAll("\\{\\{account_verification_url}}", url));
  }

  private void assertCanSendNewMail(@NonNull Timestamp verificationSentAt) throws ResponseStatusException {
    if (verificationSentAt.toInstant().toEpochMilli() + TimeUnit.MINUTES.toMillis(MIN_EXPIRATION) >= Instant.now().toEpochMilli()) {
      throw new ResponseStatusException(HttpStatus.LOCKED);
    }
  }

  public void sendMail(@NonNull Account account) throws ResponseStatusException, IOException {
    var token = JWTUtils.create(account.getId(), MAX_EXPIRATION, SECRET);

    var content = buildMailContent(
      account.getName(),
      String.format(
        VERIFICATION_ENDPOINT_TEMPLATE,
        SERVER_HOST, SERVER_PORT, token));

    if (!emailService.send(account.getEmail(), SUBJECT, content)) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @NonNull
  public String verify(String token,
                       @NonNull Model model) {
    var target
      = Optional.ofNullable(token)
      .flatMap(_token -> JWTUtils.verify(_token, SECRET))
      .flatMap(accountJPA::findById);

    if (target.map(Account::isVerified).orElse(true))
      // invalid token or account already verified
      return "verification-error";

    var account = target.get();

    // set account as verified
    account.setVerificationSentAt(null);
    accountJPA.save(account);

    model
      .addAttribute("account_name", account.getName())
      .addAttribute("account_email", account.getEmail());

    return "verification-success";
  }

  public void reSend(@NonNull String email) throws IOException {
    var account = accountJPA.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (account.isVerified()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    assertCanSendNewMail(account.getVerificationSentAt());

    account.setVerificationSentAt(new Timestamp(System.currentTimeMillis()));
    sendMail(account);
    accountJPA.save(account);
  }

}
