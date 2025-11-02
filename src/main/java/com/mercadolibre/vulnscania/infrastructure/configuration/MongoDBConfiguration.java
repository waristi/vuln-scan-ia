package com.mercadolibre.vulnscania.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 * Enables Spring Data MongoDB repositories in the infrastructure layer.
 */
@Configuration
@EnableMongoRepositories(
    basePackages = "com.mercadolibre.vulnscania.infrastructure.adapter.output.persistence.mongodb.repository"
)
public class MongoDBConfiguration {
}

