package com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;
import com.mercadolibre.vulnscania.domain.port.output.ApplicationRepository;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.document.ApplicationDocument;
import com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository.SpringDataApplicationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementing ApplicationRepository port.
 * Translates between domain and MongoDB concerns.
 */
@Component
public class MongoApplicationRepositoryAdapter implements ApplicationRepository {
    
    private final SpringDataApplicationRepository springRepository;
    
    public MongoApplicationRepositoryAdapter(SpringDataApplicationRepository springRepository) {
        this.springRepository = springRepository;
    }
    
    @Override
    public Application save(Application application) {
        ApplicationDocument document = ApplicationDocument.fromDomain(application);
        ApplicationDocument saved = springRepository.save(document);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Application> findById(ApplicationId id) {
        return springRepository.findById(id.value())
            .map(ApplicationDocument::toDomain);
    }
    
    @Override
    public Optional<Application> findByName(String name) {
        return springRepository.findByName(name)
            .map(ApplicationDocument::toDomain);
    }
    
    @Override
    public List<Application> findAll() {
        return springRepository.findAll().stream()
            .map(ApplicationDocument::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Application> findCriticalApplications() {
        return springRepository.findCriticalApplications().stream()
            .map(ApplicationDocument::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(ApplicationId id) {
        springRepository.deleteById(id.value());
    }
    
    @Override
    public boolean existsById(ApplicationId id) {
        return springRepository.existsById(id.value());
    }
    
    @Override
    public boolean existsByName(String name) {
        return springRepository.existsByName(name);
    }
}

