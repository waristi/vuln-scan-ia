package com.mercadolibre.vulnscania.domain.port.output;

import com.mercadolibre.vulnscania.domain.event.DomainEvent;

/**
 * Output Port: DomainEventPublisher
 * Define el contrato para publicar eventos de dominio sin acoplar al framework.
 */
public interface DomainEventPublisher {
    
    /**
     * Publica un evento de dominio.
     */
    void publish(DomainEvent event);
    
    /**
     * Publica múltiples eventos de dominio.
     */
    default void publishAll(Iterable<DomainEvent> events) {
        events.forEach(this::publish);
    }
}

