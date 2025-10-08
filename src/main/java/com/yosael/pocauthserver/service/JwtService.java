package com.yosael.pocauthserver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final RSAPrivateKey privateKey;

  @Value("${app.jwt.issuer}")
  private String issuer;

  @Value("${app.jwt.access-token-minutes}")
  private long accessMinutes;

  @Value("${app.jwt.key-id}")
  private String keyId;

  public String generateAccessToken(Authentication auth) throws JOSEException {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(accessMinutes * 60);

    List<String> roles = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .map(a -> a.replace("ROLE_", "")) // e.g. ADMIN
        .toList();

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(auth.getName())
        .issuer(issuer)
        .issueTime(Date.from(now))
        .expirationTime(Date.from(exp))
        .jwtID(UUID.randomUUID().toString())
        .claim("roles", roles)
        .build();

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
        .keyID(keyId)
        .type(JOSEObjectType.JWT)
        .build();

    SignedJWT jwt = new SignedJWT(header, claims);
    jwt.sign(new RSASSASigner(privateKey));
    return jwt.serialize();
  }
}
