package com.yosael.pocauthserver.service;

import com.yosael.pocauthserver.entity.UserAccount;
import com.yosael.pocauthserver.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

  private final UserAccountRepository users;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserAccount ua = users.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    return User.withUsername(ua.getUsername())
        .password(ua.getPasswordHash())
        .disabled(!ua.isEnabled())
        .authorities(ua.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
            .collect(Collectors.toSet()))
        .build();
  }
}
