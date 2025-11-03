package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.model.auth.User;
import com.mercadolibre.vulnscania.domain.model.auth.UserId;

import java.util.Optional;

/**
 * Output Port for User persistence operations.
 * 
 * <p>Defines the contract for user repository operations, keeping the domain
 * layer independent of persistence technology (MongoDB, PostgreSQL, etc.).</p>
 */
public interface UserRepository {
    
    /**
     * Finds a user by username.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by ID.
     * 
     * @param userId The user ID
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(UserId userId);
    
    /**
     * Saves a user (create or update).
     * 
     * @param user The user to save
     * @return The saved user (with updated timestamps if applicable)
     */
    User save(User user);
    
    /**
     * Checks if a username already exists.
     * 
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
}

