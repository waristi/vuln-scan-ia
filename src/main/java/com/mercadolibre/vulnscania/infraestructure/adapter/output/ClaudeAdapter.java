package com.mercadolibre.vulnscania.infraestructure.adapter.output;

import com.mercadolibre.vulnscania.domain.port.output.IAServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(prefix = "ai.claude", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class ClaudeAdapter implements IAServicePort {

    @Value("${ai.claude.model:claude-3-5-sonnet-20241022}")
    private String model;

    @Override
    public String generateResponse(String systemPrompt, String userPrompt) {
        /*MessagesCreateParams params = MessagesCreateParams.builder()
                .model(model)
                .system(systemPrompt)
                .addUserContent(userPrompt)
                .build();

        return anthropicClient.messages().create(params).content().get(0).text();*/
        return "";
    }
}


