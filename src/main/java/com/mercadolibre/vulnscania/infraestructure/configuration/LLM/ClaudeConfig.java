package com.mercadolibre.vulnscania.infraestructure.configuration.LLM;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(prefix = "ai.claude", name = "enabled", havingValue = "true")
public class ClaudeConfig {

    @Value("${ai.claude.api-key}")
    private String apiKey;

    @Value("${ai.claude.model:claude-3-5-sonnet-20241022}")
    private String model;

    @Bean
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient
                .builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public String anthropicModel() {
        return model;
    }
}


