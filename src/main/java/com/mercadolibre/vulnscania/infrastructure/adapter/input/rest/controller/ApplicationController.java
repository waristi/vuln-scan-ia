package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.controller;

import com.mercadolibre.vulnscania.application.port.input.RegisterApplicationInputPort;
import com.mercadolibre.vulnscania.application.usecase.RegisterApplicationUseCase;
import com.mercadolibre.vulnscania.domain.command.RegisterApplicationCommand;
import com.mercadolibre.vulnscania.domain.model.application.DataSensitivity;
import com.mercadolibre.vulnscania.domain.model.application.Dependency;
import com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto.RegisterApplicationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for application operations.
 * 
 * <p>This controller handles transaction management at the infrastructure layer,
 * keeping the application and domain layers framework-independent.</p>
 * 
 * <p>Note: This controller depends on Input Ports (interfaces) rather than concrete
 * use case classes, following the Dependency Inversion Principle (SOLID).</p>
 */
@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications", description = "Application management endpoints")
public class ApplicationController {
    
    private final RegisterApplicationInputPort registerPort;
    
    public ApplicationController(RegisterApplicationInputPort registerPort) {
        this.registerPort = registerPort;
    }
    
    /**
     * Registers a new application.
     * 
     * <p>Transaction management is handled here at the infrastructure layer,
     * allowing the use case to remain framework-independent.</p>
     *
     * @param request the application registration request
     * @return the registered application result
     */
    @PostMapping
    @Transactional
    @Operation(summary = "Register application", 
               description = "Registers a new application with its context information including dependencies")
    public ResponseEntity<RegisterApplicationUseCase.ApplicationResult> registerApplication(
            @Valid @RequestBody RegisterApplicationRequest request) {
        
        List<Dependency> dependencies = request.dependencies() != null
            ? request.dependencies().stream()
                .map(dto -> new Dependency(dto.name(), dto.version(), dto.ecosystem()))
                .collect(Collectors.toList())
            : List.of();
        
        RegisterApplicationCommand command = new RegisterApplicationCommand(
            request.name(),
            request.techStack(),
            dependencies,
            request.internetExposed(),
            DataSensitivity.valueOf(request.dataSensitivity()),
            request.runtimeEnvironments()
        );
        
        RegisterApplicationUseCase.ApplicationResult result = registerPort.execute(command);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
