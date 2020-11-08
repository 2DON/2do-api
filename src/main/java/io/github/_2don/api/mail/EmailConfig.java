package io.github._2don.api.mail;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class EmailConfig {

  @Value("${mail.sendgrid.api_key}")
  @Getter(AccessLevel.PACKAGE)
  private String sendGridApiKey;

  @Value("${mail.sendgrid.identity}")
  private String identity;

}
