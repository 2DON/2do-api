package io.github._2don.api.mail;

import com.sendgrid.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Service
public class EmailService {

  @Autowired
  private EmailConfig emailConfig;

  public boolean sendEmail(@NonNull String to,
                           @NotNull String subject,
                           @NonNull Content content) throws IOException {
    Mail mail = new Mail(
      new Email(emailConfig.getIdentity()),
      subject,
      new Email(to),
      content);

    SendGrid sendGrid = new SendGrid(emailConfig.getSendGridApiKey());
    Request request = new Request();

    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());

    var response = sendGrid.api(request);

    var status = response.getStatusCode();
    return status >= 200 && status < 300;
  }

}
