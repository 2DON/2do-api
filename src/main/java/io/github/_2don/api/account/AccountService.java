package io.github._2don.api.account;

import io.github._2don.api.utils.ImageEncoder;
import io.github._2don.api.utils.Patterns;
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
import java.util.Optional;

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
  public void assertExists(Long accountId, HttpStatus status) throws ResponseStatusException {
    if (!accountJPA.existsById(accountId)) {
      throw new ResponseStatusException(status);
    }
  }

  public void create(@NonNull String email,
                     @NonNull String password,
                     String name,
                     String options) throws IOException, ResponseStatusException {
    if (!Patterns.EMAIL.matches(email) || email.length() > 45
      || password.length() < 8 || password.getBytes().length > 72) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    if (accountJPA.existsByEmail(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    var account = new Account()
      .setEmail(email)
      .setPassword(bcrypt.encode(password))
      .setName(name == null ? email : name)
      .setOptions(options)
      .setVerificationSentAt(new Timestamp(System.currentTimeMillis()));

    account = accountJPA.save(account);
    verificationService.sendMail(account);
  }

  // TODO FIXME
  public Account update(Long accountId, String email, String password, String name, String options,
                        MultipartFile avatar) {

    Account account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE));

    if (email != null) {
      if (!Patterns.EMAIL.matches(email) || email.length() > 45) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      if (accountJPA.existsByEmail(email)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
      }

      account.setEmail(email);
    }

    if (password != null) {
      if (password.length() < 8) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      account.setPassword(bcrypt.encode(password));
    }

    if (name != null) {
      if (name.length() < 1 || name.length() > 45) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      account.setName(name);
    }

    if (options != null) {
      account.setOptions(options);
    }

    if (avatar != null) {
      if (!ImageEncoder.MIME_TYPES.contains(avatar.getContentType())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      try {
        account.setAvatarUrl(ImageEncoder.encodeToString(avatar.getBytes()));
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
    }

    return accountJPA.save(account);
  }

  public void fixEmail(String email, String newEmail) throws ResponseStatusException, IOException {
    var account = accountJPA.findByEmail(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (account.isVerified()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    if (!Patterns.EMAIL.matches(newEmail) || newEmail.length() > 45) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    if (accountJPA.existsByEmail(newEmail)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    verificationService.assertCanSendNewMail(account.getVerificationSentAt());

    account.setEmail(newEmail);
    account.setVerificationSentAt(new Timestamp(System.currentTimeMillis()));
    verificationService.sendMail(account);
    accountJPA.save(account);
  }

  // TODO FIXME
  /**
   * Delete User - "Request to Delete User"
   *
   * @param accountId accountId
   * @param password  password
   */
  public void delete(Long accountId, String password) throws ResponseStatusException {
    Account account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!bcrypt.matches(password, account.getPassword())) {
      // wrong password
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    account.setDeleteRequest(Date.valueOf(LocalDate.now().plusMonths(1)));
    accountJPA.save(account);
  }

  public Optional<Account> getAccount(Long accountId) {
    return accountJPA.findById(accountId);
  }

  public Optional<PublicAccount> getPublicAccount(Long accountId) {
    return accountJPA.findPublicById(accountId);
  }

  public boolean exists(String email) {
    return Patterns.EMAIL.matches(email) && accountJPA.existsByEmail(email);
  }

  /**
   * toggle premium on logged account
   *
   * @param accountId accountId
   */
  public void obtainPremium(Long accountId) throws ResponseStatusException {
    Account account = accountJPA.findById(accountId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    account.setPremium(!account.getPremium());

    accountJPA.save(account);
  }

}
