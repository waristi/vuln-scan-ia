package com.mercadolibre.vulnscania.infrastructure.adapter.output.event;

import com.mercadolibre.vulnscania.domain.event.DomainEvent;
import com.mercadolibre.vulnscania.domain.port.output.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring-based implementation of DomainEventPublisher port.
 * Bridges domain events to Spring's event mechanism.
 * 
 * Clean architecture: Domain is decoupled from Spring, this adapter provides integration.
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(SpringDomainEventPublisher.class);
    
    private final ApplicationEventPublisher springEventPublisher;
    
    public SpringDomainEventPublisher(ApplicationEventPublisher springEventPublisher) {
        this.springEventPublisher = springEventPublisher;
    }
    
    @Override
    public void publish(DomainEvent event) {
        try {
            log.debug("Publishing domain event: {}", event.getClass().getSimpleName());
            springEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish domain event: {}", event.getClass().getSimpleName(), e);
        }
    }
}

