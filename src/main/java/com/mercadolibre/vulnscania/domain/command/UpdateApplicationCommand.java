package com.mercadolibre.vulnscania.domain.command;

import com.mercadolibre.vulnscania.domain.model.application.ApplicationId;
import com.mercadolibre.vulnscania.domain.model.application.DataSensitivity;
import com.mercadolibre.vulnscania.domain.model.application.Dependency;

import java.util.List;
import java.util.Objects;

/**
 * Command: UpdateApplicationCommand
 * 
 * Represents the intention to update an existing application.
 * All fields are updated atomically.
 */
public record UpdateApplicationCommand(
    ApplicationId applicationId,
    String name,
    List<String> techStack,
    List<Dependency> dependencies,
    boolean internetExposed,
    DataSensitivity dataSensitivity,
    List<String> runtimeEnvironments
) {
    
    public UpdateApplicationCommand {
        Objects.requireNonNull(applicationId, "Application ID is required");
        Objects.requireNonNull(name, "Application name is required");
        Objects.requireNonNull(dataSensitivity, "Data sensitivity is required");
    }
}

