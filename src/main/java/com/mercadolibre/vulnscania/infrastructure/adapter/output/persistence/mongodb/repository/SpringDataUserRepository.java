package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository;

import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for User documents.
 * 
 * <p>Infrastructure layer component that provides database access.
 * This interface is automatically implemented by Spring Data MongoDB.</p>
 */
@Repository
public interface SpringDataUserRepository extends MongoRepository<UserDocument, String> {
    
    /**
     * Finds a user by username.
     * 
     * @param username The username to search for
     * @return Optional containing the user document if found
     */
    Optional<UserDocument> findByUsername(String username);
    
    /**
     * Checks if a user exists by username.
     * 
     * @param username The username to check
     * @return true if the username exists
     */
    boolean existsByUsername(String username);
}

