package com.mercadolibre.vulnscania.infrastructure.adapter.output.api.nvd;

import java.util.List;

/**
 * NVD API response DTOs.
 * These are infrastructure concerns and don't leak into domain.
 */
public record NvdApiResponse(
    List<VulnerabilityItem> vulnerabilities
) {
    
    public record VulnerabilityItem(
        Cve cve
    ) {}
    
    public record Cve(
        String id,
        String sourceIdentifier,
        String published,
        String lastModified,
        String vulnStatus,
        List<Description> descriptions,
        Metrics metrics,
        List<Reference> references
    ) {}
    
    public record Description(
        String lang,
        String value
    ) {}
    
    public record Metrics(
        List<CvssMetricV31> cvssMetricV31,
        List<CvssMetricV30> cvssMetricV30,
        List<CvssMetricV2> cvssMetricV2
    ) {}
    
    public record CvssMetricV31(
        String source,
        String type,
        CvssData cvssData
    ) {}
    
    public record CvssMetricV30(
        String source,
        String type,
        CvssData cvssData
    ) {}
    
    public record CvssMetricV2(
        String source,
        String type,
        CvssV2Data cvssData
    ) {}
    
    public record CvssData(
        String version,
        String vectorString,
        String attackVector,
        String attackComplexity,
        String privilegesRequired,
        String userInteraction,
        String scope,
        String confidentialityImpact,
        String integrityImpact,
        String availabilityImpact,
        double baseScore,
        String baseSeverity
    ) {}
    
    public record CvssV2Data(
        String version,
        String vectorString,
        String accessVector,
        String accessComplexity,
        String authentication,
        String confidentialityImpact,
        String integrityImpact,
        String availabilityImpact,
        double baseScore
    ) {}
    
    public record Reference(
        String url,
        String source
    ) {}
}

