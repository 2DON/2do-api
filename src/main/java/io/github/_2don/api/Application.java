package io.github._2don.api;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github._2don.api.security.JWTAuthenticationProvider;

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

  @Bean
  CommandLineRunner createAvatarFolder(@Value("${avatar_folder}") String avatarFolder) {
    return args -> new File(avatarFolder).mkdirs();
  }
}
