package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository;

import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document.ApplicationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for ApplicationDocument.
 * This is framework-specific and stays in infrastructure layer.
 */
public interface SpringDataApplicationRepository extends MongoRepository<ApplicationDocument, String> {
    
    Optional<ApplicationDocument> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("{ 'internetExposed': true, 'dataSensitivity': { $in: ['SENSITIVE', 'HIGHLY_REGULATED'] }, 'runtimeEnvironments': { $in: ['prod', 'production'] } }")
    List<ApplicationDocument> findCriticalApplications();
}

