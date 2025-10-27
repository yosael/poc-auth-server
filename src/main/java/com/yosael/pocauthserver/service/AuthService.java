package com.yosael.pocauthserver.service;

import com.yosael.pocauthserver.config.JwtProperties;
import com.yosael.pocauthserver.dto.LoginRequest;
import com.yosael.pocauthserver.dto.RegisterRequest;
import com.yosael.pocauthserver.dto.TokenResponse;
import com.yosael.pocauthserver.entity.Role;
import com.yosael.pocauthserver.entity.UserAccount;
import com.yosael.pocauthserver.exception.UsernameAlreadyExistsException;
import com.yosael.pocauthserver.repository.RoleRepository;
import com.yosael.pocauthserver.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserAccountRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder encoder;
  private final JwtProperties jwtProps;
  private final String USER_ROLE = "USER";

  @Transactional(readOnly = true)
  public TokenResponse login(LoginRequest req) throws Exception {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.username(), req.password()));

    String token = jwtService.generateAccessToken(auth);
    long expires = Duration.ofMinutes(jwtProps.getAccessTokenMinutes()).toSeconds();
    return new TokenResponse(token, "Bearer", expires);
  }

  @Transactional
  public void register(RegisterRequest req) {

    if (users.existsByUsername(req.username())) {
      throw new UsernameAlreadyExistsException("Username already exists");
    }

    Role userRole = roles.findByName(USER_ROLE).orElseGet(() -> roles.save(Role.builder().name(USER_ROLE).build()));

    try {
      users.save(UserAccount.builder()
          .username(req.username())
          .passwordHash(encoder.encode(req.password()))
          .enabled(true)
          .roles(Set.of(userRole))
          .build());
    } catch (DataIntegrityViolationException e) {
      // race-condition safe: if two requests create same username at once
      throw new UsernameAlreadyExistsException("Username already exists");
    }
  }
}
