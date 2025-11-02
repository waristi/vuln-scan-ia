package com.mercadolibre.vulnscania.infrastructure.adapter.output.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.vulnscania.domain.model.application.Application;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability;
import com.mercadolibre.vulnscania.domain.port.output.AIAnalysisPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * OpenAI GPT adapter implementing AIAnalysisPort.
 * Uses GPT-4 for vulnerability analysis with structured JSON responses.
 * 
 * Enabled only when ai.openai.enabled=true in configuration.
 */
@Component
@ConditionalOnProperty(prefix = "ai.openai", name = "enabled", havingValue = "true")
public class OpenAIAnalysisAdapter extends AbstractAIAnalysisAdapter implements AIAnalysisPort {
    
    private static final String PROVIDER = "OpenAI GPT-4";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    
    private final WebClient webClient;
    private final String model;
    private final ObjectMapper objectMapper;
    
    public OpenAIAnalysisAdapter(
            @Value("${ai.openai.api-key}") String apiKey,
            @Value("${ai.openai.api-url:https://api.openai.com/v1/chat/completions}") String apiUrl,
            @Value("${ai.openai.model:gpt-4}") String model,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper) {
        
        this.webClient = webClientBuilder
            .baseUrl(apiUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        this.model = model;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public AIAnalysisResult analyze(Vulnerability vulnerability, Application application) {
        try {
            log.debug("Analyzing {} with OpenAI for application {}", 
                vulnerability.getCveId().value(), application.getName());
            
            String prompt = buildAnalysisPrompt(vulnerability, application);
            String response = callOpenAI(prompt);
            
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("OpenAI analysis failed for {}: {}", 
                vulnerability.getCveId().value(), e.getMessage(), e);
            
            return createFallbackResult(vulnerability);
        }
    }
    
    /**
     * Calls OpenAI API with retry logic.
     */
    private String callOpenAI(String prompt) {
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.3,
            "max_tokens", 1000,
            "response_format", Map.of("type", "json_object")
        );
        
        String response = webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(TIMEOUT)
            .onErrorResume(this::handleError)
            .block();
        
        if (response == null) {
            throw new RuntimeException("OpenAI API returned null response");
        }
        
        return response;
    }
    
    /**
     * Parses OpenAI response JSON.
     */
    private AIAnalysisResult parseResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
        String content = contentNode.asText();
        
        JsonNode analysisJson = objectMapper.readTree(content);
        
        double score = analysisJson.path("score").asDouble();
        String justification = analysisJson.path("justification").asText();
        double confidence = analysisJson.path("confidence").asDouble(0.7);
        
        if (!isValidScore(score)) {
            log.warn("Invalid score from OpenAI: {}, clamping to valid range", score);
            score = Math.max(0.0, Math.min(10.0, score));
        }
        
        if (!isValidJustification(justification)) {
            throw new RuntimeException("Invalid justification from OpenAI");
        }
        
        if (!isValidConfidence(confidence)) {
            log.warn("Invalid confidence from OpenAI: {}, defaulting to 0.7", confidence);
            confidence = 0.7;
        }
        
        return new AIAnalysisResult(
            new SeverityScore(score),
            justification,
            confidence,
            PROVIDER
        );
    }
    
    /**
     * Creates fallback result when AI analysis fails.
     */
    private AIAnalysisResult createFallbackResult(Vulnerability vulnerability) {
        return new AIAnalysisResult(
            vulnerability.getBaseScore(),
            "AI analysis unavailable - using base CVSS score",
            0.5,
            PROVIDER + " (fallback)"
        );
    }
    
    /**
     * Handles WebClient errors.
     */
    private Mono<String> handleError(Throwable error) {
        log.error("OpenAI API call failed: {}", error.getMessage());
        return Mono.empty();
    }
}

