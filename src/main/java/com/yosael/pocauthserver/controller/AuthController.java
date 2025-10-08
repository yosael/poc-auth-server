package com.yosael.pocauthserver.controller;

import com.yosael.pocauthserver.dto.LoginRequest;
import com.yosael.pocauthserver.dto.RegisterRequest;
import com.yosael.pocauthserver.dto.TokenResponse;
import com.yosael.pocauthserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public TokenResponse login(@RequestBody @Valid LoginRequest req) throws Exception {
    return authService.login(req);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/register")
  public void register(@RequestBody @Valid RegisterRequest req) {
    authService.register(req);
  }
}
