package io.github._2don.api.security;

import io.github._2don.api.repositories.AccountJPA;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

@Data
@Accessors(chain = true)
public class Credentials implements AccountJPA.CredentialsProjection {

  private Long id;
  private String email;
  private String password;
  private Date deleteRequest;

}
