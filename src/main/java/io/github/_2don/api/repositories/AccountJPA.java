package io.github._2don.api.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github._2don.api.models.Account;

public interface AccountJPA extends JpaRepository<Account, Long> {
  boolean existsByEmail(String email);

  Optional<CredentialsProjection> findByEmail(String email);

  public static interface CredentialsProjection {

    Long getId();

    String getEmail();

    String getPassword();

  }

}
