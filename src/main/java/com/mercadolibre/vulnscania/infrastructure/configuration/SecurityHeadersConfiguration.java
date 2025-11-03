package com.mercadolibre.vulnscania.infrastructure.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Configuration for HTTP security headers following OWASP best practices.
 *
 * <p>This filter adds security headers to all HTTP responses to prevent common
 * web vulnerabilities such as XSS, clickjacking, MIME sniffing, and others.</p>
 *
 * <p><strong>Headers Added</strong>:</p>
 * <ul>
 *   <li><strong>X-Content-Type-Options: nosniff</strong> - Prevents MIME type sniffing</li>
 *   <li><strong>X-Frame-Options: DENY</strong> - Prevents clickjacking attacks</li>
 *   <li><strong>X-XSS-Protection: 1; mode=block</strong> - Enables browser XSS protection</li>
 *   <li><strong>Strict-Transport-Security</strong> - Forces HTTPS (when enabled in production)</li>
 *   <li><strong>Content-Security-Policy</strong> - Restricts resource loading</li>
 *   <li><strong>Referrer-Policy: strict-origin-when-cross-origin</strong> - Controls referrer information</li>
 * </ul>
 *
 * <p><strong>Note</strong>: This is an infrastructure concern and does not affect
 * the domain or application layers, maintaining hexagonal architecture principles.</p>
 */
@Configuration
public class SecurityHeadersConfiguration extends OncePerRequestFilter {

    private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    private static final String X_FRAME_OPTIONS = "X-Frame-Options";
    private static final String X_XSS_PROTECTION = "X-XSS-Protection";
    private static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String REFERRER_POLICY = "Referrer-Policy";
    private static final String PERMISSIONS_POLICY = "Permissions-Policy";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Prevent MIME type sniffing
        response.setHeader(X_CONTENT_TYPE_OPTIONS, "nosniff");

        // Prevent clickjacking
        response.setHeader(X_FRAME_OPTIONS, "DENY");

        // Enable browser XSS protection (legacy but still useful)
        response.setHeader(X_XSS_PROTECTION, "1; mode=block");

        // Force HTTPS in production (only add if using HTTPS)
        // Uncomment and configure for production:
        // response.setHeader(STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains");

        // Content Security Policy - restrict resource loading
        // Adjust based on your needs (Swagger UI requires inline scripts)
        response.setHeader(CONTENT_SECURITY_POLICY,
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " + // Swagger UI requires unsafe-inline/eval
            "style-src 'self' 'unsafe-inline'; " + // Swagger UI requires unsafe-inline
            "img-src 'self' data: https:; " +
            "font-src 'self' data:; " +
            "connect-src 'self' https://services.nvd.nist.gov https://generativelanguage.googleapis.com https://api.openai.com https://api.anthropic.com; " +
            "frame-ancestors 'none';"
        );

        // Control referrer information
        response.setHeader(REFERRER_POLICY, "strict-origin-when-cross-origin");

        // Permissions Policy - restrict browser features
        response.setHeader(PERMISSIONS_POLICY,
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(), " +
            "usb=(), " +
            "magnetometer=(), " +
            "gyroscope=(), " +
            "accelerometer=()"
        );

        filterChain.doFilter(request, response);
    }
}

