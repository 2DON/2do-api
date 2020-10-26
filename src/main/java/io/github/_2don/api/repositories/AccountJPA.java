package io.github._2don.api.repositories;

import io.github._2don.api.models.Account;
import io.github._2don.api.projections.PublicAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.Optional;

public interface AccountJPA extends JpaRepository<Account, Long> {
  boolean existsByEmail(String email);

  Optional<CredentialsProjection> findByEmail(String email);

  Optional<PublicAccount> findPublicById(Long id);

  interface CredentialsProjection {

    Long getId();

    String getEmail();

    String getPassword();

    Date getDeleteRequest();

  }

}
