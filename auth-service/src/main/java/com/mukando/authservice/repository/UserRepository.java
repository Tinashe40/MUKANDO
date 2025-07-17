package com.mukando.authservice.repository;

import java.util.Optional;

import com.mukando.commons.jpa.BaseDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mukando.authservice.model.User;
@Repository
public interface UserRepository extends BaseDao<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
