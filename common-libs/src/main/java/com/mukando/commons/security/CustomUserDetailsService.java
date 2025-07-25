package com.mukando.commons.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface  CustomUserDetailsService {
  /**
   *
   * @param username
   * @return UserDetails
   * @throws UsernameNotFoundException
   */
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
