package com.yosael.pocauthserver.controller;

import com.yosael.pocauthserver.dto.RoleDto;
import com.yosael.pocauthserver.dto.UserSummaryDto;
import com.yosael.pocauthserver.service.AdminQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSearchController {

  private final AdminQueryService adminQueryService;

  // GET /api/admin/users?q=ali&role=ADMIN&enabled=true&page=0&size=20&sort=username,asc
  @GetMapping("/users")
  public Page<UserSummaryDto> searchUsers(
      @RequestParam(required = false, name = "q") String query,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Boolean enabled,
      @PageableDefault(size = 20, sort = "username") Pageable pageable) {
    return adminQueryService.searchUsers(query, role, enabled, pageable);
  }

  // GET /api/admin/roles?q=ad&page=0&size=20&sort=name,asc
  @GetMapping("/roles")
  public Page<RoleDto> searchRoles(
      @RequestParam(required = false, name = "q") String query,
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    return adminQueryService.searchRoles(query, pageable);
  }
}

