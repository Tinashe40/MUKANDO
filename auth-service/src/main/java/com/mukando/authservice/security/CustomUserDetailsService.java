package com.mukando.authservice.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mukando.authservice.service.UserService;
import com.mukando.commons.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class   CustomUserDetailsService implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    try {
        return userService.findByUsername(username);
    } catch (NotFoundException e) {
      throw new UsernameNotFoundException("Invalid username or password.");
    }

  }
}
