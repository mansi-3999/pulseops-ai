package com.mansi.pulseops.ai.controller;

import com.mansi.pulseops.ai.dto.AiAnalysisResponse;
import com.mansi.pulseops.ai.service.AiIncidentAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai-analyses")
public class AiAnalysisController {

    private final AiIncidentAnalysisService service;

    public AiAnalysisController(
            AiIncidentAnalysisService service
    ) {
        this.service = service;
    }

    @PostMapping("/incidents/{incidentId}")
    public ResponseEntity<AiAnalysisResponse> analyze(
            @PathVariable UUID incidentId
    ) {
        return ResponseEntity.ok(
                service.analyzeIncident(incidentId)
        );
    }

    @GetMapping("/incidents/{incidentId}")
    public ResponseEntity<AiAnalysisResponse>
    getLatest(
            @PathVariable UUID incidentId
    ) {
        return ResponseEntity.ok(
                service.getLatestByIncident(incidentId)
        );
    }
}