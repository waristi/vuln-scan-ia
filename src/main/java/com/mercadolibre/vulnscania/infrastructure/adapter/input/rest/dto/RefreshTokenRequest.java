package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * REST API DTO for refresh token request.
 * 
 * @param refreshToken The refresh token to use for generating new tokens
 */
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}

