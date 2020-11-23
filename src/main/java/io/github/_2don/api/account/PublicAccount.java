package io.github._2don.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface PublicAccount {
  static PublicAccount from(Account account) {
    return new PublicAccount.Impl(
      account.getId(),
      account.getEmail(),
      account.getName(),
      account.getAvatarUrl(),
      account.getVerificationSentAt(),
      account.getPremium()
    );
  }

  Long getId();

  String getEmail();

  String getName();

  String getAvatarUrl();

  Timestamp getVerificationSentAt();

  Boolean getPremium();

  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class Impl implements PublicAccount {
    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private Timestamp verificationSentAt;
    private Boolean premium;
  }
}
