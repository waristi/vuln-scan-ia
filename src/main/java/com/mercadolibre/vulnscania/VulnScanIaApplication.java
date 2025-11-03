package com.mercadolibre.vulnscania;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for VulnScan IA - AI-Assisted Vulnerability Assessment System.
 * 
 * <p>This is the entry point of the Spring Boot application. It provides an API backend
 * that uses AI (LLMs) to assist in the analysis and evaluation of security vulnerabilities
 * on open-source dependencies, considering vulnerability descriptions and the technological
 * context of the potentially affected application.</p>
 * 
 * <p><strong>Key Features</strong>:</p>
 * <ul>
 *   <li>Vulnerability severity evaluation using deterministic CVSS scoring</li>
 *   <li>AI-enhanced analysis using multiple providers (OpenAI, Claude, Gemini)</li>
 *   <li>Contextual assessment based on application characteristics</li>
 *   <li>Human review flagging for high-risk or uncertain assessments</li>
 * </ul>
 * 
 * @author Bernardo Zuluaga
 * @since 1.0.0
 */
@SpringBootApplication
public class VulnScanIaApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(VulnScanIaApplication.class, args);
    }

}
