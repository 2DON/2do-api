package io.github._2don.api.account;

import io.github._2don.api.utils.ImageEncoder;
import io.github._2don.api.utils.Patterns;
import io.github._2don.api.utils.Status;
import io.github._2don.api.verification.VerificationService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Service
public class AccountService {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private BCryptPasswordEncoder bcrypt;
  @Autowired
  private VerificationService verificationService;

  /**
   * Asserts if account exists
   *
   * @param accountId accountId
   * @param status    status
   */
  public void assertExists(@NonNull Long accountId,
                           @NonNull HttpStatus status) {
    if (!accountJPA.existsByIdAndVerificationSentAtAndDeleteRequest(accountId, null, null)) {
      throw new ResponseStatusException(status);
    }
  }

  public void create(@NonNull String email,
                     @NonNull String password,
                     String name,
                     String options) throws IOException {
    if (!Patterns.EMAIL.matches(email) || email.length() > 45
      || password.length() < 8 || password.getBytes().length > 72) {
      throw Status.BAD_REQUEST.get();
    }

    if (accountJPA.existsByEmail(email)) {
      throw Status.CONFLICT.get();
    }

    var account = new Account()
      .setEmail(email)
      .setPassword(bcrypt.encode(password))
      .setOptions(options)
      .setVerificationSentAt(new Timestamp(System.currentTimeMillis()));

    if (name == null) {
      account.setName(email);
    } else if (name.length() >= 1 && name.length() <= 45) {
      account.setName(name);
    } else {
      throw Status.BAD_REQUEST.get();
    }

    account = accountJPA.save(account);
    verificationService.sendMail(account);
  }

  public Account update(@NonNull Long accountId,
                        String email,
                        String password,
                        String name,
                        String options) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.GONE);

    if (email != null) {
      if (!Patterns.EMAIL.matches(email) || email.length() > 45) {
        throw Status.BAD_REQUEST.get();
      }

      if (accountJPA.existsByEmail(email)) {
        throw Status.CONFLICT.get();
      }

      account.setEmail(email);
    }

    if (password != null) {
      if (password.length() < 8 || password.getBytes().length > 72) {
        throw Status.BAD_REQUEST.get();
      }

      account.setPassword(bcrypt.encode(password));
    }

    if (name != null) {
      if (name.length() < 1 || name.length() > 45) {
        throw Status.BAD_REQUEST.get();
      }

      account.setName(name);
    }

    if (options != null) {
      account.setOptions(options.length() == 0 ? null : options);
    }

    return accountJPA.save(account);
  }

  public Account updateAvatar(@NonNull Long accountId,
                              MultipartFile avatar) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.GONE);

    if (avatar == null || avatar.isEmpty()) {
      account.setAvatarUrl(null);
    } else {
      if (!ImageEncoder.supports(avatar.getContentType())) {
        throw Status.BAD_REQUEST.get();
      }

      try {
        account.setAvatarUrl(ImageEncoder.encodeToString(avatar.getBytes()));
      } catch (IOException e) {
        throw Status.BAD_REQUEST.get();
      }
    }

    account = accountJPA.save(account);
    return account;
  }

  public void fixEmail(@NonNull String email,
                       @NonNull String newEmail) throws IOException {
    var account = accountJPA.findByEmail(email).orElseThrow(Status.NOT_FOUND);

    if (account.isVerified()) {
      throw Status.UNAUTHORIZED.get();
    }

    if (!Patterns.EMAIL.matches(newEmail) || newEmail.length() > 45) {
      throw Status.BAD_REQUEST.get();
    }

    if (accountJPA.existsByEmail(newEmail)) {
      throw Status.CONFLICT.get();
    }

    verificationService.assertCanSendNewMail(account.getVerificationSentAt());

    account.setEmail(newEmail);
    account.setVerificationSentAt(new Timestamp(System.currentTimeMillis()));
    verificationService.sendMail(account);
    accountJPA.save(account);
  }

  /**
   * Delete User - "Request to Delete User"
   *
   * @param accountId accountId
   * @param password  password
   */
  public void delete(@NonNull Long accountId,
                     @NonNull String password) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);

    if (!bcrypt.matches(password, account.getPassword())) {
      // wrong password
      throw Status.UNAUTHORIZED.get();
    }

    account.setDeleteRequest(Date.valueOf(LocalDate.now().plusMonths(1)));
    accountJPA.save(account);
  }

  /**
   * toggle premium on logged account
   *
   * @param accountId accountId
   */
  public void obtainPremium(@NonNull Long accountId) {
    var account = accountJPA.findById(accountId).orElseThrow(Status.NOT_FOUND);

    account.setPremium(!account.getPremium());

    accountJPA.save(account);
  }

}
