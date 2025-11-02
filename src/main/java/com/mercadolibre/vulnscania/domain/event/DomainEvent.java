package com.mercadolibre.vulnscania.domain.event;

import java.time.Instant;

/**
 * Interfaz base para todos los eventos de dominio.
 * Los eventos representan hechos que ya ocurrieron en el dominio.
 */
public interface DomainEvent {
    
    /**
     * Timestamp de cuándo ocurrió el evento.
     */
    Instant occurredAt();
}

