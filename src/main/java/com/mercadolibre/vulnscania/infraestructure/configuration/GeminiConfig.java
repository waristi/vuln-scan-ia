package com.mercadolibre.vulnscania.infraestructure.configuration;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(prefix = "ai.gemini", name = "enabled", havingValue = "true")
public class GeminiConfig {

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-1.5-pro}")
    private String model;

    @Bean
    public Client geminiModel() {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public String geminiModelName() {
        return model;
    }
}


