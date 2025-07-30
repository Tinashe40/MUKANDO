package com.mukando.userservice.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mukando.commons.jpa.BaseDao;
import com.mukando.userservice.model.User;

@Repository
public interface UserRepository extends BaseDao<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}