package com.yosael.pocauthserver.config;

import com.yosael.pocauthserver.service.DbUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final DbUserDetailsService userDetails;

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider(userDetails);
    p.setPasswordEncoder(passwordEncoder());
    return p;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
            .requestMatchers("/.well-known/jwks.json").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .build();
  }
}
