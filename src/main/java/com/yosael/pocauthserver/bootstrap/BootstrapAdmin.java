package com.yosael.pocauthserver.bootstrap;

import com.yosael.pocauthserver.entity.Role;
import com.yosael.pocauthserver.entity.UserAccount;
import com.yosael.pocauthserver.repository.RoleRepository;
import com.yosael.pocauthserver.repository.UserAccountRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"local","dev"})
class BootstrapAdmin {
  private final UserAccountRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder encoder;

  @Value("${app.bootstrap.admin.username:}")
  String adminUser;
  @Value("${app.bootstrap.admin.password:}")
  String adminPass;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    if (users.countAdmins() == 0 && !adminUser.isBlank() && !adminPass.isBlank()) {
      Role adminRole = roles.findByName("ADMIN").orElseGet(() -> roles.save(Role.builder().name("ADMIN").build()));
      users.save(UserAccount.builder()
          .username(adminUser)
          .passwordHash(encoder.encode(adminPass))
          .enabled(true)
          .roles(Set.of(adminRole))
          .build());
    }
  }
}

