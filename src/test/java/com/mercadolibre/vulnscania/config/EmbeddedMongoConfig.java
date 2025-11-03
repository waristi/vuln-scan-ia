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
import org.springframework.test.context.TestPropertySource;

/**
 * Configuration for embedded MongoDB in integration tests.
 * 
 * <p>This allows running tests without an external MongoDB instance.
 * Embedded MongoDB automatically starts on localhost:27017 when the "test" profile is active.</p>
 * 
 * <p>This configuration is only active when:
 * <ul>
 *   <li>The "test" profile is active (via @ActiveProfiles("test") on test classes)</li>
 *   <li>The test class is annotated with @SpringBootTest</li>
 * </ul>
 * </p>
 */
@TestConfiguration
@Profile("test")
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/vuln-scan-test"
})
public class EmbeddedMongoConfig {

    private static final String LOCALHOST = "localhost";
    private static final int MONGO_PORT = 27017;

    @Bean(destroyMethod = "close")
    public TransitionWalker.ReachedState<RunningMongodProcess> embeddedMongoServer() {
        try {
            return Mongod.instance()
                .withNet(Start.to(Net.class).initializedWith(
                    Net.builder()
                        .from(Net.defaults())
                        .bindIp(LOCALHOST)
                        .port(MONGO_PORT)
                        .build()))
                .start(Version.Main.V7_0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start embedded MongoDB. " +
                "If you're running tests in Docker/CI, ensure embedded MongoDB dependencies are available.", e);
        }
    }
}

