package io.github._2don.api.account;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

public interface Credentials {

  Long getId();

  String getEmail();

  String getPassword();

  Date getDeleteRequest();

  @Data
  @Accessors(chain = true)
  class Impl implements Credentials {

    private Long id;
    private String email;
    private String password;
    private Date deleteRequest;

  }
}