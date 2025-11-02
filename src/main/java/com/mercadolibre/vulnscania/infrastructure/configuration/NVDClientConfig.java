package com.mercadolibre.vulnscania.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NVDClientConfig {

    @Value("${nvd.api.url}")
    private String nvdBaseUrl;

    @Bean
    public WebClient nvdWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(nvdBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
