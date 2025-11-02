package com.mercadolibre.vulnscania.infrastructure.adapter.output.api.nvd;

import com.mercadolibre.vulnscania.domain.model.vulnerability.CveId;
import com.mercadolibre.vulnscania.domain.model.vulnerability.CvssVector;
import com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore;
import com.mercadolibre.vulnscania.domain.port.output.VulnerabilityCatalogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * NVD API adapter implementing VulnerabilityCatalogPort.
 * Fetches CVE data from National Vulnerability Database.
 * 
 * Clean code principles:
 * - Single responsibility: only NVD API communication
 * - Error handling with fallback
 * - Timeout configuration
 * - Logging for observability
 */
@Component
public class NVDApiAdapter implements VulnerabilityCatalogPort {
    
    private static final Logger log = LoggerFactory.getLogger(NVDApiAdapter.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    
    private final WebClient webClient;
    private final String nvdApiUrl;
    
    public NVDApiAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${nvd.api.url:https://services.nvd.nist.gov/rest/json/cves/2.0}") String nvdApiUrl) {
        this.webClient = webClientBuilder
            .baseUrl(nvdApiUrl)
            .build();
        this.nvdApiUrl = nvdApiUrl;
    }
    
    @Override
    public Optional<VulnerabilityCatalogData> findByCveId(CveId cveId) {
        try {
            log.debug("Fetching CVE data from NVD for: {}", cveId.value());
            
            NvdApiResponse response = webClient.get()
                .uri("?cveId={cveId}", cveId.value())
                .retrieve()
                .bodyToMono(NvdApiResponse.class)
                .timeout(TIMEOUT)
                .onErrorResume(this::handleError)
                .block();
            
            if (response == null || response.vulnerabilities() == null || response.vulnerabilities().isEmpty()) {
                log.warn("No data found for CVE: {}", cveId.value());
                return Optional.empty();
            }
            
            return mapToVulnerabilityCatalogData(cveId, response);
            
        } catch (Exception e) {
            log.error("Error fetching CVE data for {}: {}", cveId.value(), e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean exists(CveId cveId) {
        return findByCveId(cveId).isPresent();
    }
    
    /**
     * Maps NVD API response to domain VulnerabilityCatalogData.
     */
    private Optional<VulnerabilityCatalogData> mapToVulnerabilityCatalogData(
            CveId cveId, 
            NvdApiResponse response) {
        
        try {
            NvdApiResponse.VulnerabilityItem vulnItem = response.vulnerabilities().get(0);
            NvdApiResponse.Cve cve = vulnItem.cve();
            
            String description = extractDescription(cve);
            SeverityScore baseScore = extractBaseScore(cve);
            CvssVector cvssVector = extractCvssVector(cve);
            String publishedDate = cve.published();
            String lastModifiedDate = cve.lastModified();
            
            return Optional.of(new VulnerabilityCatalogData(
                cveId,
                description,
                baseScore,
                cvssVector,
                publishedDate,
                lastModifiedDate
            ));
            
        } catch (Exception e) {
            log.error("Error mapping NVD response for {}: {}", cveId.value(), e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Extracts CVE description from API response.
     */
    private String extractDescription(NvdApiResponse.Cve cve) {
        if (cve.descriptions() != null && !cve.descriptions().isEmpty()) {
            return cve.descriptions().stream()
                .filter(desc -> "en".equals(desc.lang()))
                .findFirst()
                .map(NvdApiResponse.Description::value)
                .orElse("No description available");
        }
        return "No description available";
    }
    
    /**
     * Extracts CVSS base score from API response.
     * Prioritizes CVSS v3.1, falls back to v3.0, then v2.0.
     */
    private SeverityScore extractBaseScore(NvdApiResponse.Cve cve) {
        if (cve.metrics() != null) {
            if (cve.metrics().cvssMetricV31() != null && !cve.metrics().cvssMetricV31().isEmpty()) {
                double score = cve.metrics().cvssMetricV31().get(0).cvssData().baseScore();
                return new SeverityScore(score);
            }
            if (cve.metrics().cvssMetricV30() != null && !cve.metrics().cvssMetricV30().isEmpty()) {
                double score = cve.metrics().cvssMetricV30().get(0).cvssData().baseScore();
                return new SeverityScore(score);
            }
            if (cve.metrics().cvssMetricV2() != null && !cve.metrics().cvssMetricV2().isEmpty()) {
                double score = cve.metrics().cvssMetricV2().get(0).cvssData().baseScore();
                return new SeverityScore(score);
            }
        }
        
        log.warn("No CVSS score found for CVE, defaulting to 0.0");
        return new SeverityScore(0.0);
    }
    
    /**
     * Extracts CVSS vector string from API response.
     */
    private CvssVector extractCvssVector(NvdApiResponse.Cve cve) {
        if (cve.metrics() != null) {
            if (cve.metrics().cvssMetricV31() != null && !cve.metrics().cvssMetricV31().isEmpty()) {
                String vector = cve.metrics().cvssMetricV31().get(0).cvssData().vectorString();
                return new CvssVector(vector);
            }
            if (cve.metrics().cvssMetricV30() != null && !cve.metrics().cvssMetricV30().isEmpty()) {
                String vector = cve.metrics().cvssMetricV30().get(0).cvssData().vectorString();
                return new CvssVector(vector);
            }
        }
        
        return new CvssVector("CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:N/I:N/A:N");
    }
    
    /**
     * Handles WebClient errors gracefully.
     */
    private Mono<NvdApiResponse> handleError(Throwable error) {
        log.error("NVD API call failed: {}", error.getMessage());
        return Mono.empty();
    }
}

