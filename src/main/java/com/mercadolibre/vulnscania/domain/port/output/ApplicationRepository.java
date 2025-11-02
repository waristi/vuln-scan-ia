package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port: ApplicationRepository
 * Define el contrato para persistencia de aplicaciones.
 */
public interface ApplicationRepository {
    
    Application save(Application application);
    
    Optional<Application> findById(ApplicationId id);
    
    Optional<Application> findByName(String name);
    
    List<Application> findAll();
    
    List<Application> findCriticalApplications();
    
    void deleteById(ApplicationId id);
    
    boolean existsById(ApplicationId id);
    
    boolean existsByName(String name);
}

