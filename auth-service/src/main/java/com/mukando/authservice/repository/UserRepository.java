package com.mukando.authservice.repository;

import com.mukando.authservice.model.User;
import com.mukando.commons.jpa.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Inherits basic CRUD operations from {@link BaseDao}.
 */
@Repository
public interface UserRepository extends BaseDao<User> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by username.
     *
     * @param username the username to check
     * @return true if a user exists with the given username
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email address.
     *
     * @param email the email to check
     * @return true if a user exists with the given email
     */
    boolean existsByEmail(String email);
}
