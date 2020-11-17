package io.github._2don.api.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJPA extends JpaRepository<Account, Long> {
  boolean existsByEmail(String email);

  Optional<Account> findByEmail(String email);

  Optional<PublicAccount> findPublicById(Long id);

}
