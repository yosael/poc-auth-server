package com.yosael.pocauthserver.repository;

import com.yosael.pocauthserver.entity.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findByUsername(String username);
  boolean existsByUsername(String username);
  @Query("select count(u) from UserAccount u join u.roles r where upper(r.name)='ADMIN'")
  long countAdmins();

  @Query("""
     select distinct u from UserAccount u
     left join u.roles r
     where (:q = '' or lower(u.username) like lower(concat('%', :q, '%')))
       and (:enabled is null or u.enabled = :enabled)
       and (:role = '' or upper(r.name) = upper(:role))
     """)
  Page<UserAccount> search(
      @Param("q") String q,
      @Param("role") String role,
      @Param("enabled") @Nullable Boolean enabled,
      Pageable pageable);
}
