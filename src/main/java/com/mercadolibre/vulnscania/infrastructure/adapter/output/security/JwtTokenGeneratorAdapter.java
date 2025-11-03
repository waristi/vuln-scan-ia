package com.mercadolibre.vulnscania.infrastructure.adapter.output.security;

import com.mercadolibre.vulnscania.domain.model.auth.AccessToken;
import com.mercadolibre.vulnscania.domain.model.auth.RefreshToken;
import com.mercadolibre.vulnscania.domain.port.output.TokenGeneratorPort;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Infrastructure adapter that generates and validates JWT tokens using HMAC-SHA256.
 * 
 * <p>This adapter implements token generation using JWT (JSON Web Tokens) with:
 * <ul>
 *   <li>Access tokens: 1 hour expiration</li>
 *   <li>Refresh tokens: 8 hours expiration</li>
 * </ul>
 * </p>
 * 
 * <p><strong>Security Note</strong>: Uses HMAC-SHA256 (HS256) for signing.
 * For production, consider using RS256 (RSA) for better key distribution.
 * The secret key should be stored securely (environment variable, secret manager).</p>
 */
@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {
    
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    
    private final SecretKey secretKey;
    private final long accessTokenExpirationHours;
    private final long refreshTokenExpirationHours;
    
    public JwtTokenGeneratorAdapter(
            @Value("${jwt.secret:change-this-secret-key-in-production-use-256-bits}") String secret,
            @Value("${jwt.access-token.expiration-hours:1}") long accessTokenExpirationHours,
            @Value("${jwt.refresh-token.expiration-hours:8}") long refreshTokenExpirationHours) {
        
        // Generate secret key from string (256 bits minimum for HS256)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // Ensure minimum 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            byte[] extendedKey = new byte[32];
            System.arraycopy(keyBytes, 0, extendedKey, 0, keyBytes.length);
            System.arraycopy(keyBytes, 0, extendedKey, keyBytes.length, 32 - keyBytes.length);
            keyBytes = extendedKey;
        }
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.accessTokenExpirationHours = accessTokenExpirationHours;
        this.refreshTokenExpirationHours = refreshTokenExpirationHours;
    }
    
    @Override
    public AccessToken generateAccessToken(String userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpirationHours, ChronoUnit.HOURS);
        
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .claim("userId", userId)
                .claim("tokenType", TOKEN_TYPE_ACCESS)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))
                .build();
            
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
            );
            
            signedJWT.sign(new MACSigner(secretKey));
            
            return new AccessToken(signedJWT.serialize(), expiration, userId);
            
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate access token", e);
        }
    }
    
    @Override
    public RefreshToken generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpirationHours, ChronoUnit.HOURS);
        
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .claim("userId", userId)
                .claim("tokenType", TOKEN_TYPE_REFRESH)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))
                .build();
            
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
            );
            
            signedJWT.sign(new MACSigner(secretKey));
            
            return new RefreshToken(signedJWT.serialize(), expiration, userId);
            
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }
    
    @Override
    public String validateAccessToken(String token) {
        return validateToken(token, TOKEN_TYPE_ACCESS);
    }
    
    @Override
    public String validateRefreshToken(String token) {
        return validateToken(token, TOKEN_TYPE_REFRESH);
    }
    
    /**
     * Validates a JWT token and extracts user ID if valid.
     * 
     * @param token The JWT token to validate
     * @param expectedTokenType The expected token type (access or refresh)
     * @return User ID if token is valid, null otherwise
     */
    private String validateToken(String token, String expectedTokenType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            
            // Verify signature
            if (!signedJWT.verify(new MACVerifier(secretKey))) {
                return null;
            }
            
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            
            // Verify token type
            String tokenType = claimsSet.getStringClaim("tokenType");
            if (!expectedTokenType.equals(tokenType)) {
                return null;
            }
            
            // Verify expiration
            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime != null && expirationTime.before(new Date())) {
                return null;
            }
            
            // Extract user ID
            return claimsSet.getSubject();
            
        } catch (com.nimbusds.jose.JOSEException e) {
            // Signature verification failed or JOSE-related error
            return null;
        } catch (java.text.ParseException e) {
            // Token parsing failed
            return null;
        } catch (Exception e) {
            // Unexpected error
            return null;
        }
    }
}

