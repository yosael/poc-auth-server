package com.yosael.pocauthserver.controller;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwkController {

  private final RSAPublicKey publicKey;

  @Value("${app.jwt.key-id}")
  private String keyId;

  @GetMapping("/.well-known/jwks.json")
  public Map<String, Object> jwks() {
    RSAKey jwk = new RSAKey.Builder(publicKey)
        .keyID(keyId)
        .build();
    return new JWKSet(jwk).toJSONObject();
  }
}
