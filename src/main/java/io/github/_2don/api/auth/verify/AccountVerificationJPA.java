package io.github._2don.api.auth.verify;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountVerificationJPA extends JpaRepository<AccountVerification, Long> {
}
