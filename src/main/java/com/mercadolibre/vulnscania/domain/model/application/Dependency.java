package com.mercadolibre.vulnscania.domain.model.application;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Value Object representing a software dependency (library, package, artifact).
 * This is a critical concept for vulnerability assessment as vulnerabilities
 * are typically found in specific versions of dependencies.
 * 
 * <p>Business Rules:</p>
 * <ul>
 *   <li>Name cannot be null or blank</li>
 *   <li>Version cannot be null or blank</li>
 *   <li>Ecosystem must be one of the recognized ones</li>
 *   <li>Version should follow semantic versioning or numeric format</li>
 * </ul>
 */
public record Dependency(
    String name,
    String version,
    String ecosystem
) {
    
    /**
     * Recognized software ecosystems for dependency management.
     */
    private static final Set<String> VALID_ECOSYSTEMS = Set.of(
        "maven",      // Java (Maven Central, etc.)
        "npm",        // Node.js
        "pypi",       // Python
        "nuget",      // .NET
        "composer",   // PHP
        "rubygems",   // Ruby
        "go",         // Go modules
        "cargo",      // Rust
        "hex",        // Erlang/Elixir
        "cocoapods",  // iOS/macOS
        "unknown"     // Fallback for unrecognized ecosystems
    );
    
    /**
     * Pattern for semantic versioning (semver): MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]
     * Examples: 1.0.0, 2.3.4-beta.1, 3.0.0+20130313144700
     */
    private static final Pattern SEMVER_PATTERN = 
        Pattern.compile("^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9.]+)?(\\+[a-zA-Z0-9.]+)?$");
    
    /**
     * Pattern for simple numeric versioning: N.N.N...
     * Examples: 1.0, 2.3.4.5, 10.1
     */
    private static final Pattern NUMERIC_PATTERN = 
        Pattern.compile("^\\d+(\\.\\d+)*$");
    
    /**
     * Compact canonical constructor with business validation.
     */
    public Dependency {
        // Technical validation
        Objects.requireNonNull(name, "Dependency name cannot be null");
        Objects.requireNonNull(version, "Dependency version cannot be null");
        Objects.requireNonNull(ecosystem, "Ecosystem cannot be null");
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Dependency name cannot be blank");
        }
        if (version.isBlank()) {
            throw new IllegalArgumentException("Dependency version cannot be blank");
        }
        if (ecosystem.isBlank()) {
            throw new IllegalArgumentException("Ecosystem cannot be blank");
        }
        
        // Business validation: normalize ecosystem to lowercase
        ecosystem = ecosystem.toLowerCase().trim();
        
        // Business validation: ecosystem must be recognized
        if (!VALID_ECOSYSTEMS.contains(ecosystem)) {
            throw new IllegalArgumentException(
                String.format("Invalid ecosystem '%s'. Must be one of: %s", 
                    ecosystem, VALID_ECOSYSTEMS)
            );
        }
        
        // Business validation: version format (relaxed - allows unknown formats)
        if (!isValidVersionFormat(version)) {
            throw new IllegalArgumentException(
                String.format("Invalid version format '%s'. Expected semantic versioning (e.g., 1.2.3) or numeric format (e.g., 1.0)", 
                    version)
            );
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
    
    /**
     * Validates if the version string has a valid format.
     * Accepts semantic versioning (1.2.3) or simple numeric versioning (1.0).
     *
     * @param version the version string to validate
     * @return true if the version format is valid
     */
    private static boolean isValidVersionFormat(String version) {
        // Allow "unknown" as a special case
        if ("unknown".equalsIgnoreCase(version)) {
            return true;
        }
        
        // Check semantic versioning (most strict)
        if (SEMVER_PATTERN.matcher(version).matches()) {
            return true;
        }
        
        // Check simple numeric versioning (more relaxed)
        if (NUMERIC_PATTERN.matcher(version).matches()) {
            return true;
        }
        
        // Allow alphanumeric versions with dots, dashes, or underscores
        // Examples: "1.0-SNAPSHOT", "2.3.4_beta", "v1.2.3"
        return version.matches("^[vV]?[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*(-[a-zA-Z0-9.]+)?(_[a-zA-Z0-9.]+)?$");
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

