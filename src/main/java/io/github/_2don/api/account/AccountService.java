package io.github._2don.api.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

  @Autowired
  private AccountJPA accountJPA;

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

}
