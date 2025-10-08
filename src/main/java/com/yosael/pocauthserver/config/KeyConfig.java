package com.yosael.pocauthserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class KeyConfig {

  @Value("${app.jwt.public-key-pem}")
  private String publicKeyPem;

  @Value("${app.jwt.private-key-pem}")
  private String privateKeyPem;

  @Bean
  public RSAPublicKey rsaPublicKey() throws Exception {
    String pem = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");
    byte[] der = Base64.getDecoder().decode(pem);
    return (RSAPublicKey) KeyFactory.getInstance("RSA")
        .generatePublic(new X509EncodedKeySpec(der));
  }

  @Bean
  public RSAPrivateKey rsaPrivateKey() throws Exception {
    String pem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
    byte[] der = Base64.getDecoder().decode(pem);
    return (RSAPrivateKey) KeyFactory.getInstance("RSA")
        .generatePrivate(new PKCS8EncodedKeySpec(der));
  }
}
