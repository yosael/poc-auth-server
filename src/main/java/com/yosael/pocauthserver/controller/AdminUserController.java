package com.yosael.pocauthserver.controller;

import com.yosael.pocauthserver.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

  private final AdminUserService adminUserService;

  @PutMapping("/{username}/roles/{role}")
  public void grantRole(@PathVariable String username, @PathVariable String role) {
    adminUserService.grantRole(username, role);
  }

  @DeleteMapping("/{username}/roles/{role}")
  public void revokeRole(@PathVariable String username, @PathVariable String role) {
    adminUserService.revokeRole(username, role);
  }
}

