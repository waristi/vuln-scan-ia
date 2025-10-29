package com.mercadolibre.vulnscania.infraestructure.adapter.output;

import com.mercadolibre.vulnscania.domain.port.output.IAServicePort;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIAdapter implements IAServicePort {

    private final OpenAIClient openAiClient;

    @Value("${ai.openai.model}")
    private String model;

    @Value("${ai.openai.temperature}")
    private Double temperature;

    public String generateResponse(String systemPrompt, String userPrompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Say this is a test")
                .model(ChatModel.GPT_5)
                .build();
        ChatCompletion chatCompletion = openAiClient.chat().completions().create(params);
        return chatCompletion.toString();
    }
}