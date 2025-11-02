package com.mercadolibre.vulnscania.domain.command;

import com.mercadolibre.vulnscania.domain.model.application.DataSensitivity;
import com.mercadolibre.vulnscania.domain.model.application.Dependency;

import java.util.List;
import java.util.Objects;

/**
 * Command: RegisterApplicationCommand
 * 
 * Represents the intention to register a new application in the system.
 * Includes all contextual information needed for vulnerability assessment.
 */
public record RegisterApplicationCommand(
    String name,
    List<String> techStack,
    List<Dependency> dependencies,
    boolean internetExposed,
    DataSensitivity dataSensitivity,
    List<String> runtimeEnvironments
) {
    
    public RegisterApplicationCommand {
        Objects.requireNonNull(name, "Application name is required");
        Objects.requireNonNull(dataSensitivity, "Data sensitivity is required");
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Application name cannot be blank");
        }
    }
}

