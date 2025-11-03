package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * REST API DTO for login request.
 * 
 * @param username User's username
 * @param password User's password (plain text)
 */
public record LoginRequest(
    @NotBlank(message = "Username is required")
    String username,
    
    @NotBlank(message = "Password is required")
    String password
) {}

