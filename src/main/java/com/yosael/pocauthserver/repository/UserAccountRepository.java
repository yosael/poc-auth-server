package com.yosael.pocauthserver.repository;

import com.yosael.pocauthserver.entity.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findByUsername(String username);
  boolean existsByUsername(String username);
}
