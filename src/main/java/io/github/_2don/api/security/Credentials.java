package io.github._2don.api.security;

import java.sql.Date;
import io.github._2don.api.repositories.AccountJPA;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Credentials implements AccountJPA.CredentialsProjection {

  private Long id;
  private String email;
  private String password;
  private Date deleteRequest;

}
