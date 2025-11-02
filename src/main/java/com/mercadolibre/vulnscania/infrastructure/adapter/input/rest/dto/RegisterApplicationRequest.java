package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * REST API DTO for application registration request.
 */
public record RegisterApplicationRequest(
    
    @NotBlank(message = "Application name is required")
    @Size(min = 3, max = 100, message = "Application name must be between 3 and 100 characters")
    String name,
    
    List<String> techStack,
    
    @Valid
    List<DependencyDTO> dependencies,
    
    @NotNull(message = "Internet exposed flag is required")
    Boolean internetExposed,
    
    @NotBlank(message = "Data sensitivity is required")
    String dataSensitivity,
    
    List<String> runtimeEnvironments
) {
    
    /**
     * Embedded DTO for dependencies.
     */
    public record DependencyDTO(
        @NotBlank(message = "Dependency name is required")
        String name,
        
        @NotBlank(message = "Dependency version is required")
        String version,
        
        String ecosystem
    ) {}
}

