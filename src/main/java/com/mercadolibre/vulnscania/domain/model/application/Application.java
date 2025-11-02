package com.mercadolibre.vulnscania.domain.model.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Aggregate Root: Application
 * 
 * Represents an application that may be affected by vulnerabilities.
 * Contains technological context information for risk assessment, including
 * the dependencies (libraries, packages) used by the application.
 * 
 * Business Rules:
 * - Application name must be between 3 and 100 characters
 * - Dependencies are crucial for vulnerability matching
 * - Risk factor is calculated based on exposure, data sensitivity, and environment
 */
public class Application {
    
    private final ApplicationId id;
    private String name;
    private List<String> techStack;
    private List<Dependency> dependencies;
    private boolean internetExposed;
    private DataSensitivity dataSensitivity;
    private List<String> runtimeEnvironments;
    private final List<String> knownMitigations;
    private final Instant createdAt;
    private Instant updatedAt;
    
    private Application(ApplicationId id,
                       String name,
                       List<String> techStack,
                       List<Dependency> dependencies,
                       boolean internetExposed,
                       DataSensitivity dataSensitivity,
                       List<String> runtimeEnvironments,
                       List<String> knownMitigations,
                       Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Application ID is required");
        this.name = validateName(name);
        this.techStack = new ArrayList<>(techStack != null ? techStack : List.of());
        this.dependencies = new ArrayList<>(dependencies != null ? dependencies : List.of());
        this.internetExposed = internetExposed;
        this.dataSensitivity = dataSensitivity != null ? dataSensitivity : DataSensitivity.INTERNAL;
        this.runtimeEnvironments = new ArrayList<>(runtimeEnvironments != null ? runtimeEnvironments : List.of());
        this.knownMitigations = new ArrayList<>(knownMitigations != null ? knownMitigations : List.of());
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = this.createdAt;
    }
    
    /**
     * Factory method to create a new application.
     *
     * @param name the application name
     * @param techStack the technology stack used
     * @param dependencies the dependencies (libraries, packages) used
     * @param internetExposed whether the application is exposed to the internet
     * @param dataSensitivity the sensitivity level of data handled
     * @param runtimeEnvironments the runtime environments (dev, staging, prod)
     * @return a new Application instance
     */
    public static Application create(String name,
                                    List<String> techStack,
                                    List<Dependency> dependencies,
                                    boolean internetExposed,
                                    DataSensitivity dataSensitivity,
                                    List<String> runtimeEnvironments) {
        return new Application(
            ApplicationId.generate(),
            name,
            techStack,
            dependencies,
            internetExposed,
            dataSensitivity,
            runtimeEnvironments,
            new ArrayList<>(),
            Instant.now()
        );
    }
    
    /**
     * Factory method for reconstitution from persistence.
     *
     * @param id the application ID
     * @param name the application name
     * @param techStack the technology stack
     * @param dependencies the dependencies list
     * @param internetExposed internet exposure flag
     * @param dataSensitivity data sensitivity level
     * @param runtimeEnvironments runtime environments
     * @param knownMitigations known mitigations
     * @param createdAt creation timestamp
     * @param updatedAt last update timestamp
     * @return a reconstituted Application instance
     */
    public static Application reconstitute(ApplicationId id,
                                          String name,
                                          List<String> techStack,
                                          List<Dependency> dependencies,
                                          boolean internetExposed,
                                          DataSensitivity dataSensitivity,
                                          List<String> runtimeEnvironments,
                                          List<String> knownMitigations,
                                          Instant createdAt,
                                          Instant updatedAt) {
        Application app = new Application(
            id,
            name,
            techStack,
            dependencies,
            internetExposed,
            dataSensitivity,
            runtimeEnvironments,
            knownMitigations,
            createdAt
        );
        app.updatedAt = updatedAt;
        return app;
    }
    
    /**
     * Updates the application information.
     *
     * @param name the new name
     * @param techStack the new technology stack
     * @param dependencies the new dependencies list
     * @param internetExposed the new internet exposure flag
     * @param dataSensitivity the new data sensitivity level
     * @param runtimeEnvironments the new runtime environments
     */
    public void update(String name,
                      List<String> techStack,
                      List<Dependency> dependencies,
                      boolean internetExposed,
                      DataSensitivity dataSensitivity,
                      List<String> runtimeEnvironments) {
        this.name = validateName(name);
        this.techStack = new ArrayList<>(techStack != null ? techStack : List.of());
        this.dependencies = new ArrayList<>(dependencies != null ? dependencies : List.of());
        this.internetExposed = internetExposed;
        this.dataSensitivity = dataSensitivity != null ? dataSensitivity : DataSensitivity.INTERNAL;
        this.runtimeEnvironments = new ArrayList<>(runtimeEnvironments != null ? runtimeEnvironments : List.of());
        this.updatedAt = Instant.now();
    }
    
    /**
     * Adds a known mitigation to the application.
     *
     * @param mitigation the mitigation description
     */
    public void addMitigation(String mitigation) {
        Objects.requireNonNull(mitigation, "Mitigation cannot be null");
        if (!mitigation.isBlank() && !this.knownMitigations.contains(mitigation)) {
            this.knownMitigations.add(mitigation);
            this.updatedAt = Instant.now();
        }
    }
    
    /**
     * Adds a dependency to the application.
     * Business rule: Dependencies are critical for vulnerability matching.
     *
     * @param dependency the dependency to add
     */
    public void addDependency(Dependency dependency) {
        Objects.requireNonNull(dependency, "Dependency cannot be null");
        if (!this.dependencies.contains(dependency)) {
            this.dependencies.add(dependency);
            this.updatedAt = Instant.now();
        }
    }
    
    /**
     * Removes a dependency from the application.
     *
     * @param dependency the dependency to remove
     */
    public void removeDependency(Dependency dependency) {
        if (this.dependencies.remove(dependency)) {
            this.updatedAt = Instant.now();
        }
    }
    
    /**
     * Checks if the application uses a specific dependency.
     *
     * @param dependencyName the dependency name to check
     * @return true if the dependency is used
     */
    public boolean usesDependency(String dependencyName) {
        return this.dependencies.stream()
            .anyMatch(dep -> dep.name().equalsIgnoreCase(dependencyName));
    }
    
    /**
     * Gets dependencies that match a specific artifact name.
     * This is critical for determining if a CVE affects this application.
     *
     * @param artifactName the artifact name to match
     * @return list of matching dependencies
     */
    public List<Dependency> getDependenciesMatching(String artifactName) {
        return this.dependencies.stream()
            .filter(dep -> dep.name().toLowerCase().contains(artifactName.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all dependencies for a specific ecosystem (maven, npm, pypi, etc.).
     *
     * @param ecosystem the ecosystem to filter by
     * @return list of dependencies in that ecosystem
     */
    public List<Dependency> getDependenciesByEcosystem(String ecosystem) {
        return this.dependencies.stream()
            .filter(dep -> dep.belongsToEcosystem(ecosystem))
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if the application runs in a specific environment.
     *
     * @param environment the environment to check (dev, staging, prod)
     * @return true if the application runs in that environment
     */
    public boolean hasEnvironment(String environment) {
        return this.runtimeEnvironments.contains(environment);
    }
    
    /**
     * Checks if the application runs in production.
     * Business rule: Production applications have higher risk priority.
     *
     * @return true if the application runs in production
     */
    public boolean isInProduction() {
        return hasEnvironment("prod") || hasEnvironment("production");
    }
    
    /**
     * Checks if the application uses a specific technology.
     *
     * @param technology the technology to check
     * @return true if the technology is in the tech stack
     */
    public boolean usesTechnology(String technology) {
        return this.techStack.stream()
            .anyMatch(tech -> tech.equalsIgnoreCase(technology));
    }
    
    /**
     * Calculates the base risk factor for the application.
     * 
     * Business rule: Combines exposure, data sensitivity, environment, and dependencies count.
     * - Internet exposed: +30%
     * - Data sensitivity: variable multiplier (1.0x - 1.8x)
     * - Production environment: +20%
     * - Known mitigations: -up to 20%
     * - High dependency count: increases risk
     *
     * @return the calculated risk factor (typically 0.8 - 3.0)
     */
    public double calculateRiskFactor() {
        double factor = 1.0;
        
        if (internetExposed) {
            factor *= 1.3;
        }
        
        factor *= dataSensitivity.getRiskMultiplier();
        
        if (isInProduction()) {
            factor *= 1.2;
        }
        
        double mitigationReduction = Math.min(0.2, knownMitigations.size() * 0.05);
        factor *= (1.0 - mitigationReduction);
        
        if (dependencies.size() > 100) {
            factor *= 1.1;
        } else if (dependencies.size() > 50) {
            factor *= 1.05;
        }
        
        return factor;
    }
    
    /**
     * Determines if the application is considered critical infrastructure.
     * 
     * Business rule: Critical if it handles sensitive data, is exposed to internet,
     * and runs in production.
     *
     * @return true if the application is critical infrastructure
     */
    public boolean isCriticalInfrastructure() {
        return internetExposed && 
               dataSensitivity.requiresSpecialProtection() && 
               isInProduction();
    }
    
    private String validateName(String name) {
        Objects.requireNonNull(name, "Application name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Application name cannot be blank");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Application name must be at least 3 characters");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Application name must not exceed 100 characters");
        }
        return name;
    }
    
    public List<Dependency> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }
    public ApplicationId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getTechStack() {
        return Collections.unmodifiableList(techStack);
    }
    
    public boolean isInternetExposed() {
        return internetExposed;
    }
    
    public DataSensitivity getDataSensitivity() {
        return dataSensitivity;
    }
    
    public List<String> getRuntimeEnvironments() {
        return Collections.unmodifiableList(runtimeEnvironments);
    }
    
    public List<String> getKnownMitigations() {
        return Collections.unmodifiableList(knownMitigations);
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

