package io.github._2don.api.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.github._2don.api.models.Account;

public interface AccountJPA extends JpaRepository<Account, Long> {
  boolean existsByEmail(String email);

  Optional<Account> findByEmail(String email);

  @Query("select account.id from Account as account where account.email = :email")
  Long getId(@Param("email") String email);
}
