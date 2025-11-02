package com.mercadolibre.vulnscania.infrastructure.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient configuration for external API calls.
 * Configures connection pooling, timeouts, and error handling.
 */
@Configuration
public class WebClientConfiguration {
    
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 30;
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(30);
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
            .responseTimeout(RESPONSE_TIMEOUT)
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT, TimeUnit.SECONDS))
            );
        
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}

