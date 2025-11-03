package com.mercadolibre.vulnscania;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/vuln-scan-test",
    "ai.fallback.enabled=true"
})
@ActiveProfiles("test")
class VulnScanIaApplicationTests {

    @Test
    void contextLoads() {
    }

}
