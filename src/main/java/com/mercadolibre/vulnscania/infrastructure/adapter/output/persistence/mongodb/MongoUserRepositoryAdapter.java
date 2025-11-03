package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb;

import com.mercadolibre.vulnscania.domain.model.auth.User;
import com.mercadolibre.vulnscania.domain.model.auth.UserId;
import com.mercadolibre.vulnscania.domain.port.output.UserRepository;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document.UserDocument;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Infrastructure adapter that implements UserRepository port using MongoDB.
 * 
 * <p>This adapter converts between domain User entities and MongoDB UserDocument,
 * keeping the domain layer independent of persistence technology.</p>
 */
@Component
public class MongoUserRepositoryAdapter implements UserRepository {
    
    private final SpringDataUserRepository repository;
    
    public MongoUserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
            .map(this::toDomain);
    }
    
    @Override
    public Optional<User> findById(UserId userId) {
        return repository.findById(userId.value())
            .map(this::toDomain);
    }
    
    @Override
    public User save(User user) {
        UserDocument document = toDocument(user);
        UserDocument saved = repository.save(document);
        return toDomain(saved);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
    
    /**
     * Converts MongoDB document to domain entity.
     */
    private User toDomain(UserDocument document) {
        User user = User.create(
            document.getUsername(),
            document.getPasswordHash(),
            document.getEmail()
        );
        
        // Use reflection or builder pattern to set private fields
        // For now, we'll recreate with all fields
        
        // Since User doesn't have setters, we need to handle this differently
        // Let's use a different approach - modify User to accept all fields
        return restoreUser(
            UserId.of(document.getId()),
            document.getUsername(),
            document.getPasswordHash(),
            document.getEmail(),
            document.isActive(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }
    
    /**
     * Converts domain entity to MongoDB document.
     */
    private UserDocument toDocument(User user) {
        return new UserDocument(
            user.getId().value(),
            user.getUsername(),
            user.getPasswordHash(),
            user.getEmail(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    /**
     * Restores a User from persisted state.
     */
    private User restoreUser(UserId id, String username, String passwordHash, String email,
                            boolean active, java.time.Instant createdAt, java.time.Instant updatedAt) {
        return User.restore(id, username, passwordHash, email, active, createdAt, updatedAt);
    }
}

