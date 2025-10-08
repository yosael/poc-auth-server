package com.yosael.pocauthserver.config;

import lombok.Getter; import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
  private String issuer;
  private long accessTokenMinutes = 15;
  private long refreshTokenDays = 7;
  private String publicKeyPem;
  private String privateKeyPem;
  private String keyId;
}

