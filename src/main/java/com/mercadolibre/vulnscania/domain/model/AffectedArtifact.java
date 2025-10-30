package com.mercadolibre.vulnscania.domain.model;

public record AffectedArtifact(
        String criteria,
        String part,
        String vendor,
        String product,
        String version,
        String versionStartIncluding,
        String versionStartExcluding,
        String versionEndIncluding,
        String versionEndExcluding,
        boolean vulnerable
) { }
