-- Database initialization script for VulnScan IA

-- Create database (if not already created by Docker)
CREATE DATABASE IF NOT EXISTS vulnscan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE vulnscan;

-- Create evaluations table
CREATE TABLE IF NOT EXISTS evaluations (
    id VARCHAR(36) PRIMARY KEY,
    cve_id VARCHAR(50) NOT NULL,
    application_name VARCHAR(255) NOT NULL,
    business_domain VARCHAR(100),
    severity VARCHAR(20) NOT NULL,
    base_score DECIMAL(3,1) NOT NULL,
    adjusted_score DECIMAL(3,1) NOT NULL,
    confidence_score DECIMAL(3,2) NOT NULL,
    justification TEXT,
    requires_immediate_attention BOOLEAN DEFAULT FALSE,
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_cve (cve_id),
    INDEX idx_application (application_name),
    INDEX idx_severity (severity),
    INDEX idx_evaluated_at (evaluated_at)
);

-- Create vulnerabilities table (if you need to store CVE details)
CREATE TABLE IF NOT EXISTS vulnerabilities (
    cve_id VARCHAR(50) PRIMARY KEY,
    description TEXT,
    cvss_base_score DECIMAL(3,1),
    attack_vector VARCHAR(50),
    attack_complexity VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cve_id (cve_id)
);
