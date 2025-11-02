package com.mercadolibre.vulnscania.domain.model.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class DependencyTest {
    
    @Test
    void shouldCreateValidDependency() {
        // When
        Dependency dependency = new Dependency("spring-boot", "3.1.0", "maven");
        
        // Then
        assertThat(dependency.name()).isEqualTo("spring-boot");
        assertThat(dependency.version()).isEqualTo("3.1.0");
        assertThat(dependency.ecosystem()).isEqualTo("maven");
    }
    
    @Test
    void shouldNormalizeEcosystemToLowercase() {
        // When
        Dependency dependency = new Dependency("spring-boot", "3.1.0", "MAVEN");
        
        // Then
        assertThat(dependency.ecosystem()).isEqualTo("maven");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"maven", "npm", "pypi", "nuget", "composer", "rubygems", "go", "cargo", "hex", "cocoapods", "unknown"})
    void shouldAcceptValidEcosystems(String ecosystem) {
        // When/Then
        assertThatNoException().isThrownBy(() -> 
            new Dependency("test-package", "1.0.0", ecosystem));
    }
    
    @Test
    void shouldRejectInvalidEcosystem() {
        // When/Then
        assertThatThrownBy(() -> new Dependency("test", "1.0.0", "invalid-ecosystem"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid ecosystem");
    }
    
    @ParameterizedTest
    @CsvSource({
        "1.0.0,          true",      // Simple semver
        "2.3.4,          true",      // Semver
        "1.2.3-beta.1,   true",      // Semver with prerelease
        "3.0.0+build123, true",      // Semver with build
        "1.0,            true",      // Numeric
        "2.3.4.5,        true",      // Extended numeric
        "1.0-SNAPSHOT,   true",      // Maven snapshot
        "v1.2.3,         true",      // With v prefix
        "2.3.4_beta,     true",      // With underscore
        "unknown,        true"       // Special case
    })
    void shouldAcceptValidVersionFormats(String version, boolean expected) {
        // When/Then
        if (expected) {
            assertThatNoException().isThrownBy(() ->
                new Dependency("test", version, "maven"));
        }
    }
    
    @Test
    void shouldRejectNullName() {
        // When/Then
        assertThatThrownBy(() -> new Dependency(null, "1.0.0", "maven"))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void shouldRejectBlankName() {
        // When/Then
        assertThatThrownBy(() -> new Dependency("   ", "1.0.0", "maven"))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void shouldRejectNullVersion() {
        // When/Then
        assertThatThrownBy(() -> new Dependency("test", null, "maven"))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void shouldRejectBlankVersion() {
        // When/Then
        assertThatThrownBy(() -> new Dependency("test", "   ", "maven"))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void shouldCreateDependencyWithDetectedEcosystem() {
        // When
        Dependency mavenDep = Dependency.of("org.springframework:spring-boot", "3.1.0");
        Dependency npmDep = Dependency.of("@angular/core", "15.0.0");
        Dependency pypiDep = Dependency.of("django", "4.2.0");
        
        // Then
        assertThat(mavenDep.ecosystem()).isEqualTo("maven");
        assertThat(npmDep.ecosystem()).isEqualTo("npm");
        assertThat(pypiDep.ecosystem()).isEqualTo("pypi");
    }
    
    @Test
    void shouldCheckIfAffectedByArtifact() {
        // Given
        Dependency dependency = new Dependency("spring-boot", "3.1.0", "maven");
        
        // When/Then
        assertThat(dependency.isAffectedBy("spring-boot", "3.x")).isTrue();
        assertThat(dependency.isAffectedBy("spring-core", "3.x")).isFalse();
    }
    
    @Test
    void shouldCheckIfBelongsToEcosystem() {
        // Given
        Dependency dependency = new Dependency("spring-boot", "3.1.0", "maven");
        
        // When/Then
        assertThat(dependency.belongsToEcosystem("maven")).isTrue();
        assertThat(dependency.belongsToEcosystem("MAVEN")).isTrue();
        assertThat(dependency.belongsToEcosystem("npm")).isFalse();
    }
    
    @Test
    void shouldGetCoordinate() {
        // Given
        Dependency dependency = new Dependency("spring-boot", "3.1.0", "maven");
        
        // When
        String coordinate = dependency.getCoordinate();
        
        // Then
        assertThat(coordinate).isEqualTo("spring-boot:3.1.0");
    }
    
    @Test
    void shouldImplementEqualsCorrectly() {
        // Given
        Dependency dep1 = new Dependency("spring-boot", "3.1.0", "maven");
        Dependency dep2 = new Dependency("spring-boot", "3.1.0", "maven");
        Dependency dep3 = new Dependency("spring-boot", "3.2.0", "maven");
        
        // When/Then
        assertThat(dep1).isEqualTo(dep2);
        assertThat(dep1).isNotEqualTo(dep3);
        assertThat(dep1.hashCode()).isEqualTo(dep2.hashCode());
    }
}

