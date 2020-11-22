package io.github._2don.api.mail;

import com.sendgrid.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Service
public class EmailService {

  private final String API_KEY;
  private final String IDENTITY;

  public EmailService(@Value("${mail.sendgrid.api_key}") String apiKey,
                      @Value("${mail.sendgrid.identity}") String identity) {
    API_KEY = apiKey;
    IDENTITY = identity;
  }

  public Response send(@NonNull Mail mail) throws IOException {
    SendGrid sendGrid = new SendGrid(API_KEY);
    Request request = new Request();

    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());

    return sendGrid.api(request);
  }

  public boolean send(@NonNull String to,
                      @NotNull String subject,
                      @NonNull Content content) throws IOException {
    var response = send(
      new Mail(new Email(IDENTITY), subject, new Email(to), content));

    var status = response.getStatusCode();
    return status >= 200 && status < 300;
  }

}
