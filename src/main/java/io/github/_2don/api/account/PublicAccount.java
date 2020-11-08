package io.github._2don.api.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

public interface PublicAccount {
  static PublicAccount from(Account account) {
    return new PublicAccount.Impl(
      account.getId(),
      account.getEmail(),
      account.getName(),
      account.getAvatarUrl(),
      account.getPremium()
    );
  }

  Long getId();

  String getEmail();

  String getName();

  String getAvatarUrl();

  Boolean getPremium();

  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  class Impl implements PublicAccount {
    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private Boolean premium;
  }
}
