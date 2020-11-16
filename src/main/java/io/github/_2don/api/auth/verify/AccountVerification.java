package io.github._2don.api.auth.verify;

import io.github._2don.api.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
public class AccountVerification {

  @Id
  @Column(name = "account_id")
  private Long id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private Timestamp minExp;

}
