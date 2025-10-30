package com.mercadolibre.vulnscania.infraestructure.configuration.LLM;

import com.mercadolibre.vulnscania.domain.port.output.IAServicePort;
import com.mercadolibre.vulnscania.infraestructure.adapter.output.LLM.ClaudeAdapter;
import com.mercadolibre.vulnscania.infraestructure.adapter.output.LLM.GeminiAdapter;
import com.mercadolibre.vulnscania.infraestructure.adapter.output.LLM.OpenAIAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;

@Configuration
public class LLMConfig {

    @Value("${ai.provider.active:openai}")
    private String activeProvider;

    @Bean
    @Primary
    public IAServicePort llmServicePort(ObjectProvider<OpenAIAdapter> openAIAdapter,
                                        ObjectProvider<ClaudeAdapter> claudeAdapter,
                                        ObjectProvider<GeminiAdapter> geminiAdapter) {
        return switch (activeProvider.toLowerCase()) {
            case "claude" -> requireBean(claudeAdapter, "ClaudeAdapter", "ai.claude.enabled");
            case "gemini" -> requireBean(geminiAdapter, "GeminiAdapter", "ai.gemini.enabled");
            default -> requireBean(openAIAdapter, "OpenAIAdapter", "ai.openai.enabled");
        };
    }

    private static <T extends IAServicePort> IAServicePort requireBean(ObjectProvider<T> provider,
                                                                       String beanName,
                                                                       String flagProperty) {
        T bean = provider.getIfAvailable();
        if (bean == null) {
            throw new IllegalStateException("Bean not available: " + beanName + ". Ensure property '" + flagProperty + "=true' and credentials are set.");
        }
        return bean;
    }
}


