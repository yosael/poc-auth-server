package com.yosael.pocauthserver.service;

import com.yosael.pocauthserver.entity.Role;
import com.yosael.pocauthserver.entity.UserAccount;
import com.yosael.pocauthserver.repository.RoleRepository;
import com.yosael.pocauthserver.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

  private final UserAccountRepository users;
  private final RoleRepository roles;

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void grantRole(String username, String roleName) {
    UserAccount user = users.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

    String normalized = normalize(roleName);
    Role role = roles.findByName(normalized)
        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + normalized));

    // idempotent: no-op if already present
    if (user.getRoles().stream().noneMatch(r -> r.getName().equalsIgnoreCase(normalized))) {
      user.getRoles().add(role);
      users.save(user);
    }
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void revokeRole(String username, String roleName) {
    UserAccount user = users.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

    String normalized = normalize(roleName);

    if ("ADMIN".equals(normalized) && isLastAdminRemoval(user)) {
      throw new IllegalStateException("Cannot remove the last ADMIN role");
    }

    boolean changed = user.getRoles().removeIf(r -> r.getName().equalsIgnoreCase(normalized));
    if (changed) {
      users.save(user);
    }
  }

  private String normalize(String roleName) {
    return roleName == null ? "" : roleName.trim().toUpperCase();
  }

  private boolean isLastAdminRemoval(UserAccount target) {
    boolean userHasOnlyAdmin = target.getRoles().stream()
        .filter(r -> "ADMIN".equalsIgnoreCase(r.getName()))
        .count() == 1;

    long totalAdmins = users.countAdmins();
    return userHasOnlyAdmin && totalAdmins <= 1;
  }
}
