package com.mercadolibre.vulnscania.domain.port.output;

public interface IAServicePort {
    String generateResponse(String systemPrompt, String userPrompt);
}
