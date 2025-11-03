package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representation of a User.
 * 
 * <p>This is an infrastructure adapter that maps domain User entities to MongoDB documents.
 * It handles the persistence layer concerns while keeping the domain model framework-independent.</p>
 */
@Document(collection = "users")
public class UserDocument {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    private String passwordHash;
    
    @Indexed
    private String email;
    
    private boolean active;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    public UserDocument() {}
    
    public UserDocument(String id, String username, String passwordHash, String email, 
                       boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

