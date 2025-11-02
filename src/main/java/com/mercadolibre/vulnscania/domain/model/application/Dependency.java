package com.mercadolibre.vulnscania.domain.model.application;

import java.util.Objects;

/**
 * Value Object representing a software dependency (library, package, artifact).
 * This is a critical concept for vulnerability assessment as vulnerabilities
 * are typically found in specific versions of dependencies.
 */
public record Dependency(
    String name,
    String version,
    String ecosystem
) {
    
    public Dependency {
        Objects.requireNonNull(name, "Dependency name cannot be null");
        Objects.requireNonNull(version, "Dependency version cannot be null");
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Dependency name cannot be blank");
        }
        if (version.isBlank()) {
            throw new IllegalArgumentException("Dependency version cannot be blank");
        }
    }
    
    /**
     * Creates a dependency with ecosystem detection based on name patterns.
     *
     * @param name the dependency name
     * @param version the dependency version
     * @return a new Dependency instance
     */
    public static Dependency of(String name, String version) {
        String ecosystem = detectEcosystem(name);
        return new Dependency(name, version, ecosystem);
    }
    
    /**
     * Checks if this dependency matches a given CVE's affected artifacts.
     * This is used to determine if a vulnerability applies to this specific dependency.
     *
     * @param artifactName the artifact name to match
     * @param affectedVersions the version range affected
     * @return true if this dependency is affected
     */
    public boolean isAffectedBy(String artifactName, String affectedVersions) {
        return this.name.equalsIgnoreCase(artifactName);
    }
    
    /**
     * Checks if this dependency matches the ecosystem.
     *
     * @param ecosystem the ecosystem to check
     * @return true if matches
     */
    public boolean belongsToEcosystem(String ecosystem) {
        return this.ecosystem != null && this.ecosystem.equalsIgnoreCase(ecosystem);
    }
    
    /**
     * Gets the full coordinate (e.g., "org.springframework:spring-core:5.3.0").
     */
    public String getCoordinate() {
        return name + ":" + version;
    }
    
    private static String detectEcosystem(String name) {
        if (name.contains(":")) {
            return "maven";
        } else if (name.startsWith("@") || name.contains("/")) {
            return "npm";
        } else if (name.matches("[a-z0-9-]+")) {
            return "pypi";
        }
        return "unknown";
    }
}

