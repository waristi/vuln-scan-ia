package com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.controller;

import com.mercadolibre.vulnscania.application.port.input.LoginInputPort;
import com.mercadolibre.vulnscania.application.port.input.RefreshTokenInputPort;
import com.mercadolibre.vulnscania.application.port.input.RegisterUserInputPort;
import com.mercadolibre.vulnscania.domain.command.LoginCommand;
import com.mercadolibre.vulnscania.domain.command.RefreshTokenCommand;
import com.mercadolibre.vulnscania.domain.command.RegisterUserCommand;
import com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto.LoginRequest;
import com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto.RefreshTokenRequest;
import com.mercadolibre.vulnscania.infrastructure.adapter.input.rest.dto.RegisterUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * 
 * <p>Handles user registration, login and token refresh endpoints. These endpoints are publicly
 * accessible (no authentication required) as they are used to create accounts and obtain authentication tokens.</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and token management endpoints")
public class AuthController {
    
    private final LoginInputPort loginPort;
    private final RefreshTokenInputPort refreshTokenPort;
    private final RegisterUserInputPort registerUserPort;
    
    public AuthController(
            LoginInputPort loginPort,
            RefreshTokenInputPort refreshTokenPort,
            RegisterUserInputPort registerUserPort) {
        this.loginPort = loginPort;
        this.refreshTokenPort = refreshTokenPort;
        this.registerUserPort = registerUserPort;
    }
    
    /**
     * Registers a new user in the system.
     * 
     * <p>This endpoint creates a new user account. After registration, the user
     * can use the login endpoint to obtain authentication tokens.</p>
     * 
     * @param request Registration request with user details
     * @return Registration response with user information
     */
    @PostMapping("/register")
    @Operation(summary = "Register user",
               description = "Creates a new user account. Password is automatically hashed.")
    public ResponseEntity<RegisterUserInputPort.RegistrationResult> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
            request.username(),
            request.password(),
            request.email()
        );
        RegisterUserInputPort.RegistrationResult result = registerUserPort.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * Authenticates a user and returns access and refresh tokens.
     * 
     * <p>This endpoint validates user credentials and generates:
     * <ul>
     *   <li>Access token: Valid for 1 hour</li>
     *   <li>Refresh token: Valid for 8 hours</li>
     * </ul>
     * </p>
     * 
     * @param request Login request with username and password
     * @return Login response with tokens and user information
     */
    @PostMapping("/login")
    @Operation(summary = "User login",
               description = "Authenticates a user and returns JWT access token (1 hour) and refresh token (8 hours)")
    public ResponseEntity<LoginInputPort.LoginResult> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.username(), request.password());
        LoginInputPort.LoginResult result = loginPort.execute(command);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Refreshes an access token using a refresh token.
     * 
     * <p>This endpoint allows clients to obtain a new access token without re-entering
     * credentials. Both the access token and refresh token are renewed.</p>
     * 
     * @param request Refresh token request
     * @return Token refresh response with new tokens and user information
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token",
               description = "Obtains new access and refresh tokens using a valid refresh token")
    public ResponseEntity<RefreshTokenInputPort.TokenRefreshResult> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
        RefreshTokenInputPort.TokenRefreshResult result = refreshTokenPort.execute(command);
        return ResponseEntity.ok(result);
    }
}

