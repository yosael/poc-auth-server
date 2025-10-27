package com.yosael.pocauthserver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.yosael.pocauthserver.config.JwtProperties;
import java.time.Clock;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
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
  private final JwtProperties props;
  private final Clock clock;

  public String generateAccessToken(Authentication auth) throws JOSEException {
    Instant now = Instant.now(clock);
    Instant exp = now.plus(Duration.ofMinutes(props.getAccessTokenMinutes()));

    List<String> roles = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .map(a -> a.replace("ROLE_", "")) // -> ADMIN, USER, ...
        .toList();

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(auth.getName())
        .issuer(props.getIssuer())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(exp))
        .jwtID(UUID.randomUUID().toString())
        .claim("roles", roles)
        .build();

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
        .keyID(props.getKeyId())
        .type(JOSEObjectType.JWT)
        .build();

    SignedJWT jwt = new SignedJWT(header, claims);
    jwt.sign(new RSASSASigner(privateKey));
    return jwt.serialize();
  }
}
