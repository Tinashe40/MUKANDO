package com.mukando.authservice.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mukando.authservice.model.User;
import com.mukando.commons.jpa.BaseDao;

@Repository
public interface UserRepository extends BaseDao<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
