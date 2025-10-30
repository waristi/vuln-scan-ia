package com.mercadolibre.vulnscania.infraestructure.adapter.output;

import com.google.genai.Client;
import com.mercadolibre.vulnscania.domain.port.output.IAServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(prefix = "ai.gemini", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class GeminiAdapter implements IAServicePort {

    private final Client geminiClient;

    @Value("${ai.gemini.model:gemini-1.5-pro}")
    private String model;

    @Override
    public String generateResponse(String systemPrompt, String userPrompt) {
        String prompt = systemPrompt + "\n\n" + userPrompt;
        String modelName = model.startsWith("models/") ? model : ("models/" + model);
        var response = geminiClient.models.generateContent(modelName, prompt, null);
        return response.text();
    }
}


