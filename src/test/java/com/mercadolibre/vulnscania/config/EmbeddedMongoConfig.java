package com.mercadolibre.vulnscania.config;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for embedded MongoDB in integration tests.
 * This allows running tests without an external MongoDB instance.
 */
@TestConfiguration
@Profile("test")
public class EmbeddedMongoConfig {

    private static final String LOCALHOST = "localhost";
    private static final int MONGO_PORT = 27017;

    @Bean(destroyMethod = "close")
    public TransitionWalker.ReachedState<RunningMongodProcess> embeddedMongoServer() {
        return Mongod.instance()
            .withNet(Start.to(Net.class).initializedWith(
                Net.builder()
                    .from(Net.defaults())
                    .bindIp(LOCALHOST)
                    .port(MONGO_PORT)
                    .build()))
            .start(Version.Main.V7_0);
    }
}

