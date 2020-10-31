package io.github._2don.api.account;

import com.fasterxml.jackson.databind.util.StdConverter;

public class AccountToPublicAccountConverter extends StdConverter<Account, PublicAccount> {
  @Override
  public PublicAccount convert(Account account) {
    return PublicAccount.from(account);
  }
}
