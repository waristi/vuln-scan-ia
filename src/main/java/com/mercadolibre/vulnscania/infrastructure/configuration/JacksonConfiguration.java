package com.mercadolibre.vulnscania.infrastructure.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson ObjectMapper configuration.
 * Configures JSON serialization/deserialization for the application.
 */
@Configuration
public class JacksonConfiguration {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        mapper.registerModule(new JavaTimeModule());
        
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        
        return mapper;
    }
}

