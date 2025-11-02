package com.mercadolibre.vulnscania.domain.model.application;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ApplicationTest {
    
    @Test
    void shouldRegisterNewApplication() {
        // When
        Application app = Application.create(
            "Test App",
            List.of("Java", "Spring Boot"),
            List.of(new Dependency("spring-boot", "3.1.0", "maven")),
            true,
              DataSensitivity.SENSITIVE,
            List.of("PRODUCTION")
        );
        
        // Then
        assertThat(app).isNotNull();
        assertThat(app.getName()).isEqualTo("Test App");
        assertThat(app.isInternetExposed()).isTrue();
          assertThat(app.getDataSensitivity()).isEqualTo(DataSensitivity.SENSITIVE);
    }
    
    @Test
    void shouldCalculateRiskFactor() {
        // Given
        Application app = Application.create(
            "Test App",
            List.of("Java"),
            List.of(new Dependency("test", "1.0.0", "maven")),
            true,
              DataSensitivity.SENSITIVE,
            List.of("PRODUCTION")
        );
        
        // When
        double riskFactor = app.calculateRiskFactor();
        
        // Then
        assertThat(riskFactor).isGreaterThan(0.0);
    }
    
    @Test
    void shouldIdentifyProductionEnvironment() {
        // Given
        Application prodApp = Application.create(
            "Prod App",
            List.of("Java"),
            List.of(),
            false,
            DataSensitivity.INTERNAL,
            List.of("PRODUCTION")
        );
        
        Application devApp = Application.create(
            "Dev App",
            List.of("Java"),
            List.of(),
            false,
            DataSensitivity.INTERNAL,
            List.of("DEVELOPMENT")
        );
        
        // When/Then
        assertThat(prodApp.isInProduction()).isTrue();
        assertThat(devApp.isInProduction()).isFalse();
    }
    
    @Test
    void shouldCheckIfCriticalInfrastructure() {
        // Given
        Application app = Application.create(
            "Test App",
            List.of("Java"),
            List.of(),
            false,
            DataSensitivity.HIGHLY_REGULATED,  // High sensitivity implies critical
            List.of("PRODUCTION")
        );
        
        // When/Then
        assertThat(app.isCriticalInfrastructure()).isTrue();
    }
    
    @Test
    void shouldAddAndRemoveDependencies() {
        // Given
        Application app = Application.create(
            "Test App",
            List.of("Java"),
            List.of(),
            false,
            DataSensitivity.INTERNAL,
            List.of("DEVELOPMENT")
        );
        
        Dependency dep = new Dependency("spring-boot", "3.1.0", "maven");
        
        // When
        app.addDependency(dep);
        
        // Then
        assertThat(app.usesDependency(dep.name())).isTrue();
        
        // When
        app.removeDependency(dep);
        
        // Then
        assertThat(app.usesDependency(dep.name())).isFalse();
    }
    
    @Test
    void shouldUpdateApplicationDetails() {
        // Given
        Application app = Application.create(
            "Test App",
            List.of("Java"),
            List.of(),
            false,
            DataSensitivity.INTERNAL,
            List.of("DEVELOPMENT")
        );
        
        // When
        app.update(
            "Updated App",
            List.of("Java", "Kotlin"),
            List.of(),
            true,
              DataSensitivity.SENSITIVE,
            List.of("PRODUCTION")
        );
        
        // Then
        assertThat(app.getName()).isEqualTo("Updated App");
        assertThat(app.isInternetExposed()).isTrue();
          assertThat(app.getDataSensitivity()).isEqualTo(DataSensitivity.SENSITIVE);
    }
    
    @Test
    void shouldAddMitigations() {
        // Given
        Application app = Application.create(
            "Test App",
            List.of("Java"),
            List.of(),
            false,
            DataSensitivity.INTERNAL,
            List.of("DEVELOPMENT")
        );
        
        // When
        app.addMitigation("WAF enabled");
        app.addMitigation("Input sanitization");
        
        // Then
        assertThat(app.getKnownMitigations()).hasSize(2);
    }
}

