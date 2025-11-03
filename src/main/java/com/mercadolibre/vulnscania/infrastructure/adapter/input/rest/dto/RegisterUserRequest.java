package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST API DTO for user registration request.
 * 
 * @param username Unique username (3-50 characters)
 * @param password User password (will be hashed)
 * @param email User email address
 */
public record RegisterUserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    String password,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be in valid format")
    String email
) {}

