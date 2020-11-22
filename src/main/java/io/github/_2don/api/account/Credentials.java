package io.github._2don.api.account;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class Credentials {

  private Long id;
  private String email;
  private String password;
  private Date deleteRequest;
  private Timestamp verificationSentAt;

}
