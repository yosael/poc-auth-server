package com.yosael.pocauthserver;

import com.yosael.pocauthserver.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PocAuthServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PocAuthServerApplication.class, args);
  }

}
