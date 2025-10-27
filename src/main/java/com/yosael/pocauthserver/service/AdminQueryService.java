package com.yosael.pocauthserver.service;

import com.yosael.pocauthserver.dto.RoleDto;
import com.yosael.pocauthserver.dto.UserSummaryDto;
import com.yosael.pocauthserver.entity.Role;
import com.yosael.pocauthserver.entity.UserAccount;
import com.yosael.pocauthserver.repository.RoleRepository;
import com.yosael.pocauthserver.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminQueryService {

  private final UserAccountRepository users;
  private final RoleRepository roles;

  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('ADMIN')")
  public Page<UserSummaryDto> searchUsers(String q, String role, Boolean enabled, Pageable pageable) {
    String normQ = (q == null || q.isBlank()) ? "" : q.trim();
    String normRole = (role == null || role.isBlank()) ? "" : role.trim().toUpperCase();

    Page<UserAccount> page = users.search(normQ, normRole, enabled, pageable);
    return page.map(u -> new UserSummaryDto(
        u.getId(),
        u.getUsername(),
        u.isEnabled(),
        u.getRoles().stream().map(Role::getName).sorted().toList()
    ));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('ADMIN')")
  public Page<RoleDto> searchRoles(String q, Pageable pageable) {
    String normQ = (q == null) ? "" : q.trim();
    return roles.findByNameContainingIgnoreCase(normQ, pageable)
        .map(r -> new RoleDto(r.getId(), r.getName()));
  }
}
