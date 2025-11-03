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
 * Google Gemini adapter implementing AIAnalysisPort.
 * Uses Gemini Pro for vulnerability analysis.
 * 
 * Enabled only when ai.gemini.enabled=true in configuration.
 */
@Component("geminiAdapter")
@ConditionalOnProperty(prefix = "ai.gemini", name = "enabled", havingValue = "true")
public class GeminiAnalysisAdapter extends AbstractAIAnalysisAdapter implements AIAnalysisPort {
    
    private static final String PROVIDER = "Google Gemini Pro";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    
    private final WebClient webClient;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    
    public GeminiAnalysisAdapter(
            @Value("${ai.gemini.api-key}") String apiKey,
            @Value("${ai.gemini.api-url:https://generativelanguage.googleapis.com/v1beta}") String apiUrl,
            @Value("${ai.gemini.model:gemini-2.5-flash}") String model,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper) {
        
        this.apiKey = apiKey;
        this.webClient = webClientBuilder
            .baseUrl(apiUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        this.model = model;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public AIAnalysisResult analyze(Vulnerability vulnerability, Application application) {
        try {
            log.debug("Analyzing {} with Gemini for application {}", 
                vulnerability.getCveId().value(), application.getName());
            
            String prompt = buildAnalysisPrompt(vulnerability, application);
            String response = callGemini(prompt);
            
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("Gemini analysis failed for {}: {}", 
                vulnerability.getCveId().value(), e.getMessage(), e);
            
            return createFallbackResult(vulnerability);
        }
    }
    
    /**
     * Calls Gemini API with retry logic.
     */
    private String callGemini(String prompt) {
        String fullPrompt = SYSTEM_PROMPT + "\n\n" + prompt + 
            "\n\nRespond with a JSON object containing: score (0-10), justification (detailed string), and confidence (0-1).";
        
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of(
                    "parts", List.of(
                        Map.of("text", fullPrompt)
                    )
                )
            ),
            "generationConfig", Map.of(
                "temperature", 0.3,
                "maxOutputTokens", 2048,  // Aumentado para gemini-2.5-flash
                "topP", 0.8,
                "topK", 40
            )
        );
        
        log.debug("Calling Gemini API at: {}/models/{}:generateContent", 
            webClient.toString(), model);
        
        try {
            // Gemini uses API key in URL as query parameter
            String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/models/" + model + ":generateContent")
                    .queryParam("key", apiKey)
                    .build())
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> {
                        int statusCode = clientResponse.statusCode().value();
                        log.error("Gemini API error: status code {}", statusCode);
                        return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("Gemini API error body: {}", body);
                                return Mono.error(new RuntimeException(
                                    "Gemini API error: " + statusCode + " - " + body
                                ));
                            });
                    }
                )
                .bodyToMono(String.class)
                .timeout(TIMEOUT)
                .block();
            
            if (response == null || response.isBlank()) {
                throw new RuntimeException("Gemini API returned empty response");
            }
            
            log.debug("Gemini API response received: {} characters", response.length());
            return response;
            
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses Gemini response JSON.
     * Gemini response format is different from OpenAI.
     */
    private AIAnalysisResult parseResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        
        // Log full response for debugging
        log.debug("Gemini full response: {}", response);
        
        // Gemini response structure: candidates[0].content.parts[0].text
        JsonNode candidatesNode = root.path("candidates");
        if (candidatesNode.isEmpty()) {
            throw new RuntimeException("No candidates in Gemini response");
        }
        
        JsonNode firstCandidate = candidatesNode.get(0);
        
        // Check finish reason
        String finishReason = firstCandidate.path("finishReason").asText("");
        if ("MAX_TOKENS".equals(finishReason)) {
            log.warn("Gemini response truncated due to MAX_TOKENS. Consider increasing maxOutputTokens.");
        }
        
        JsonNode contentNode = firstCandidate.path("content").path("parts");
        if (contentNode.isEmpty() || contentNode.get(0) == null) {
            log.error("No content parts in Gemini response. FinishReason: {}", finishReason);
            throw new RuntimeException(
                "No content in Gemini response. FinishReason: " + finishReason + 
                ". The model may have hit token limits or content filters."
            );
        }
        
        JsonNode textNode = contentNode.get(0).path("text");
        if (textNode.isMissingNode() || textNode.asText().isEmpty()) {
            log.error("Empty text in Gemini response. FinishReason: {}", finishReason);
            throw new RuntimeException(
                "Empty text in Gemini response. FinishReason: " + finishReason +
                ". Try reducing prompt size or increasing maxOutputTokens."
            );
        }
        
        String textContent = textNode.asText();
        log.debug("Gemini text content: {}", textContent);
        
        // Extract JSON from markdown code blocks if present
        textContent = extractJsonFromMarkdown(textContent);
        
        JsonNode analysisJson = objectMapper.readTree(textContent);
        
        double score = analysisJson.path("score").asDouble();
        String justification = analysisJson.path("justification").asText();
        double confidence = analysisJson.path("confidence").asDouble(0.7);
        
        if (!isValidScore(score)) {
            log.warn("Invalid score from Gemini: {}, clamping to valid range", score);
            score = Math.max(0.0, Math.min(10.0, score));
        }
        
        if (!isValidJustification(justification)) {
            throw new RuntimeException("Invalid justification from Gemini");
        }
        
        if (!isValidConfidence(confidence)) {
            log.warn("Invalid confidence from Gemini: {}, defaulting to 0.7", confidence);
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
     * Extracts JSON from markdown code blocks.
     * Gemini sometimes wraps JSON in ```json ... ```
     */
    private String extractJsonFromMarkdown(String text) {
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.indexOf("```", start);
            if (end > start) {
                return text.substring(start, end).trim();
            }
        } else if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.indexOf("```", start);
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        return text.trim();
    }
    
    /**
     * Creates fallback result when AI analysis fails.
     */
    private AIAnalysisResult createFallbackResult(Vulnerability vulnerability) {
        return new AIAnalysisResult(
            vulnerability.getBaseScore(),
            "Gemini AI analysis unavailable - using base CVSS score",
            0.5,
            PROVIDER + " (fallback)"
        );
    }
    
}
