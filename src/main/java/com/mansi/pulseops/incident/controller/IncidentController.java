/**
 * REST endpoints for creating, retrieving, and managing
 * operational incidents.
 */

package com.mansi.pulseops.incident.controller;

import com.mansi.pulseops.incident.domain.IncidentStatus;
import com.mansi.pulseops.incident.domain.Severity;
import com.mansi.pulseops.incident.dto.CreateIncidentRequest;
import com.mansi.pulseops.incident.dto.IncidentResponse;
import com.mansi.pulseops.incident.dto.UpdateIncidentStatusRequest;
import com.mansi.pulseops.incident.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incidents", description = "Incident lifecycle APIs")
public class IncidentController {
    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new incident")
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/incidents/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public IncidentResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public List<IncidentResponse> getAll(@RequestParam(required = false) IncidentStatus status, @RequestParam(required = false) Severity severity) {
        return service.getAll(status, severity);
    }

    @PatchMapping("/{id}/status")
    public IncidentResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateIncidentStatusRequest request) {
        return service.updateStatus(id, request.status());
    }
}
