package io.github._2don.api;

import io.github._2don.api.security.JWTAuthenticationProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  BCryptPasswordEncoder bcrypt() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JWTAuthenticationProvider authenticationProvider() {
    return new JWTAuthenticationProvider();
  }

}
