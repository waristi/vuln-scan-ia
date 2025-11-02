package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;
import com.mercadolibre.vulnscania.domain.model.application.DataSensitivity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoDB document for Application aggregate.
 * Follows hexagonal architecture by being framework-specific in infrastructure layer.
 */
@Document(collection = "applications")
public class ApplicationDocument {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    private List<String> techStack;
    private List<DependencyDocument> dependencies;
    private boolean internetExposed;
    private String dataSensitivity;
    private List<String> runtimeEnvironments;
    private List<String> knownMitigations;
    private Instant createdAt;
    private Instant updatedAt;
    
    public ApplicationDocument() {
    }
    
    /**
     * Converts domain Application to document.
     */
    public static ApplicationDocument fromDomain(Application application) {
        ApplicationDocument doc = new ApplicationDocument();
        doc.id = application.getId().value();
        doc.name = application.getName();
        doc.techStack = application.getTechStack();
        doc.dependencies = application.getDependencies().stream()
            .map(DependencyDocument::fromDomain)
            .collect(Collectors.toList());
        doc.internetExposed = application.isInternetExposed();
        doc.dataSensitivity = application.getDataSensitivity().name();
        doc.runtimeEnvironments = application.getRuntimeEnvironments();
        doc.knownMitigations = application.getKnownMitigations();
        doc.createdAt = application.getCreatedAt();
        doc.updatedAt = application.getUpdatedAt();
        return doc;
    }
    
    /**
     * Converts document to domain Application.
     */
    public Application toDomain() {
        return Application.reconstitute(
            ApplicationId.of(id),
            name,
            techStack,
            dependencies.stream()
                .map(DependencyDocument::toDomain)
                .collect(Collectors.toList()),
            internetExposed,
            DataSensitivity.valueOf(dataSensitivity),
            runtimeEnvironments,
            knownMitigations,
            createdAt,
            updatedAt
        );
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}

