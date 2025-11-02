package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document;

import com.mercadolibre.vulnscania.domain.model.application.Dependency;

/**
 * MongoDB document representation of a Dependency.
 * This is an embedded document within ApplicationDocument.
 */
public record DependencyDocument(
    String name,
    String version,
    String ecosystem
) {
    
    /**
     * Converts domain Dependency to document.
     */
    public static DependencyDocument fromDomain(Dependency dependency) {
        return new DependencyDocument(
            dependency.name(),
            dependency.version(),
            dependency.ecosystem()
        );
    }
    
    /**
     * Converts document to domain Dependency.
     */
    public Dependency toDomain() {
        return new Dependency(name, version, ecosystem);
    }
}

