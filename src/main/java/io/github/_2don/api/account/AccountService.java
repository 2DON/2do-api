package io.github._2don.api.account;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import io.github._2don.api.utils.ImageEncoder;
import io.github._2don.api.utils.Patterns;

@Service
public class AccountService {

  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private BCryptPasswordEncoder bcrypt;
  @Autowired
  private AccountToPublicAccountConverter publicAccountConverter;

  /**
   * Create Account
   *
   * @param email    String
   * @param password String
   * @return Account
   */
  public Account add(Account account) {
    account.setPremium(false);

    if (account.getEmail() == null || !Patterns.EMAIL.matches(account.getEmail())
        || account.getEmail().length() > 45 || account.getPassword() == null
        || account.getPassword().length() < 8) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    if (accountJPA.existsByEmail(account.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    account.setPassword(bcrypt.encode(account.getPassword())).setName(account.getEmail())
        .setOptions(null);

    return accountJPA.save(account);
  }

  /**
   * Update User
   *
   * @param accountId Long
   * @param email     String
   * @param password  String
   * @param name      String
   * @param options   String
   * @param avatar    MultipartFile
   * @return Account
   */
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

  /**
   * Delete User - "Request to Delete User"
   *
   * @param accountId
   * @param password
   */
  public void delete(Long accountId, String password) {
    Account account = accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!bcrypt.matches(password, account.getPassword())) {
      // wrong password
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    account.setDeleteRequest(Date.valueOf(LocalDate.now().plusMonths(1)));
    accountJPA.save(account);
  }

  /**
   * Get User Info
   *
   * @param accountId Long
   * @param isPublic  boolean
   * @return JSONObject
   */
  public Account getAccount(Long accountId) {
    return accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  public PublicAccount getPublicAccount(Long accountId) {
    Account account = accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    return this.publicAccountConverter.convert(account);
  }

  /**
   * Asserts if account exists
   *
   * @param accountId accountId
   * @param status    status
   */
  public void assertExists(Long accountId, HttpStatus status) {
    if (!accountJPA.existsById(accountId)) {
      throw new ResponseStatusException(status);
    }
  }

  /**
   * Asserts if account exists
   *
   * @param accountId accountId
   */
  public boolean exist(String email) {
    if (!Patterns.EMAIL.matches(email))
      return false;
    return accountJPA.existsByEmail(email);
  }

  /**
   * Set Premium on User
   *
   * @param accountId
   */
  public void obtainPremium(Long accountId) {
    Account account = accountJPA.findById(accountId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (account.getPremium()) {
      account.setPremium(false);
    } else {
      account.setPremium(true);
    }

    accountJPA.save(account);
  }
}
