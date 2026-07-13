/**
 * REST endpoints for retrieving deterministic incident
 * investigations and supporting evidence.
 */

package com.mansi.pulseops.investigation.controller;

import com.mansi.pulseops.investigation.dto.InvestigationResponse;
import com.mansi.pulseops.investigation.service.InvestigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/investigations")
public class InvestigationController {

    private final InvestigationService investigationService;

    public InvestigationController(
            InvestigationService investigationService
    ) {
        this.investigationService = investigationService;
    }

    @PostMapping("/incidents/{incidentId}")
    public ResponseEntity<InvestigationResponse> investigate(
            @PathVariable UUID incidentId
    ) {
        return ResponseEntity.ok(
                investigationService.investigate(incidentId)
        );
    }

    @GetMapping("/incidents/{incidentId}")
    public ResponseEntity<InvestigationResponse>
    getLatestInvestigation(
            @PathVariable UUID incidentId
    ) {
        return ResponseEntity.ok(
                investigationService
                        .getLatestByIncident(incidentId)
        );
    }
}