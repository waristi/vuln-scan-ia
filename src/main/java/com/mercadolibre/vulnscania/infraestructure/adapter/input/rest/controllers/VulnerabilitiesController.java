package com.mercadolibre.vulnscania.infraestructure.adapter.input.rest.controllers;

import com.mercadolibre.vulnscania.application.service.VulnerabilityAnalysisService;
import com.mercadolibre.vulnscania.infraestructure.adapter.input.rest.dto.VulnerabilityAnalysisRequest;
import com.mercadolibre.vulnscania.infraestructure.adapter.input.rest.dto.VulnerabilityEvaluationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vulnerabilities")
@RequiredArgsConstructor
public class VulnerabilitiesController {

    private final VulnerabilityAnalysisService vulnerabilityAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<VulnerabilityEvaluationResponse> analyzeCode(@Valid @RequestBody VulnerabilityAnalysisRequest request) {
        String response = vulnerabilityAnalysisService.analyzeCode(request.cveId());
        VulnerabilityEvaluationResponse resp = new VulnerabilityEvaluationResponse(
                1.0,
                "CRITIAL",
                "TEST",
                response,
                1.0,
                1.0
        );
        return ResponseEntity.ok(resp);
    }
}
