package com.mercadolibre.vulnscania.domain.model.auth;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a User in the system.
 * 
 * <p>This entity contains user authentication information and follows DDD patterns.
 * It is responsible for validating credentials and managing user authentication state.</p>
 * 
 * <p>The password is stored as a hash (handled by infrastructure layer) to ensure
 * security. The domain layer only deals with password validation logic.</p>
 */
public class User {
    
    private UserId id;
    private String username;
    private String passwordHash;
    private String email;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Private constructor to enforce factory methods.
     */
    private User() {}
    
    /**
     * Creates a new user.
     * 
     * @param username Unique username. Must not be null or blank.
     * @param passwordHash Hashed password. Must not be null or blank.
     * @param email User email. Must not be null or blank.
     * @return New User instance
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static User create(String username, String passwordHash, String email) {
        User user = new User();
        user.id = UserId.of(username); // Using username as ID for simplicity
        user.username = validateUsername(username);
        user.passwordHash = validatePasswordHash(passwordHash);
        user.email = validateEmail(email);
        user.active = true;
        Instant now = Instant.now();
        user.createdAt = now;
        user.updatedAt = now;
        return user;
    }
    
    /**
     * Restores a user from persistence.
     * 
     * <p>This method is used by infrastructure adapters to recreate User entities
     * from persisted data, preserving all fields including timestamps.</p>
     * 
     * @param id User ID
     * @param username Username
     * @param passwordHash Hashed password
     * @param email Email
     * @param active Whether the user is active
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     * @return Restored User instance
     */
    public static User restore(UserId id, String username, String passwordHash, String email,
                               boolean active, Instant createdAt, Instant updatedAt) {
        User user = new User();
        user.id = id;
        user.username = validateUsername(username);
        user.passwordHash = validatePasswordHash(passwordHash);
        user.email = validateEmail(email);
        user.active = active;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }
    
    /**
     * Validates user credentials against the stored password hash.
     * 
     * <p>Note: Actual password comparison is done in the infrastructure layer
     * using a password encoder (BCrypt, Argon2, etc.). This method is a placeholder
     * for domain-level validation logic.</p>
     * 
     * @param password The plain text password to validate
     * @return true if password matches (validation done by infrastructure)
     * @throws IllegalArgumentException if password is null or blank
     */
    public boolean validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        
        // Actual password comparison is done by infrastructure layer
        // This method exists for domain-level validation logic if needed
        return true;
    }
    
    /**
     * Activates the user account.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Updates the user's password hash.
     * 
     * @param newPasswordHash The new hashed password
     * @throws IllegalArgumentException if passwordHash is invalid
     */
    public void updatePasswordHash(String newPasswordHash) {
        this.passwordHash = validatePasswordHash(newPasswordHash);
        this.updatedAt = Instant.now();
    }
    
    private static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        return username;
    }
    
    private static String validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
        return passwordHash;
    }
    
    private static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must be in valid format");
        }
        return email;
    }
    
    // Getters
    
    public UserId getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

