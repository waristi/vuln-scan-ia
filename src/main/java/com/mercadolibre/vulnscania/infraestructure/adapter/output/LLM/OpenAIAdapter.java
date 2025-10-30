package com.mercadolibre.vulnscania.infraestructure.adapter.output.LLM;

import com.mercadolibre.vulnscania.domain.port.output.IAServicePort;
import com.openai.client.OpenAIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(prefix = "ai.openai", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class OpenAIAdapter implements IAServicePort {

    private final OpenAIClient openAiClient;

    @Value("${ai.openai.model}")
    private String model;

    @Value("${ai.openai.temperature}")
    private Double temperature;

    public String generateResponse(String systemPrompt, String userPrompt) {
        /*ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.from(model))
                .addMessage(ChatMessageParam.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content(systemPrompt)
                        .build())
                .addMessage(ChatMessageParam.builder()
                        .role(ChatMessageRole.USER)
                        .content(userPrompt)
                        .build())
                .temperature(temperature)
                .build();
        ChatCompletion chatCompletion = openAiClient.chat().completions().create(params);
        return chatCompletion.choices().get(0).message().content();*/
        return "";
    }
}