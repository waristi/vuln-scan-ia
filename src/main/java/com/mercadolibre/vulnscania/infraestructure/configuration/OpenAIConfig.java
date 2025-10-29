package com.mercadolibre.vulnscania.infraestructure.configuration;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.model}")
    private String model;

    @Value("${ai.openai.temperature}")
    private Double temperature;

    @Bean
    public OpenAIClient openAiClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public String openAiModel() {
        return model;
    }

    @Bean
    public Double openAiTemperature() {
        return temperature;
    }
}