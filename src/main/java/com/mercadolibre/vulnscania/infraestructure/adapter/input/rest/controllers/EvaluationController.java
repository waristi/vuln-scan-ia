package com.mercadolibre.vulnscania.infraestructure.adapter.input.rest.controllers;

import com.mercadolibre.vulnscania.application.service.VulnerabilityAnalysisService;
import com.mercadolibre.vulnscania.domain.port.output.IAServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final VulnerabilityAnalysisService vulnerabilityAnalysisService;

    @GetMapping("")
    public ResponseEntity<String> getEvaluation() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String language = request.getOrDefault("language", "java");

        String result = vulnerabilityAnalysisService.analyzeCode(code, language);
        return ResponseEntity.ok(result);
    }
}
