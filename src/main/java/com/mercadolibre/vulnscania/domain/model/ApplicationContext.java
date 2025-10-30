package com.mercadolibre.vulnscania.domain.model;

import java.util.List;

public record ApplicationContext(
    String appName,
    List<String> techStack,
    boolean internetExposed,
    String dataSensitivity,
    List<String> runtimeEnvs,
    List<String> knownMitigations
) { }
