package io.github._2don.api.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountJPA extends JpaRepository<Account, Long> {

  boolean existsByEmail(String email);

  Optional<Account> findByEmail(String email);

  List<PublicAccount> findAllPublicByIdIn(Iterable<Long> id);

}
