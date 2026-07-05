package com.mansi.pulseops.investigation.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record InvestigationResponse(
        UUID id,
        UUID incidentId,
        String status,
        String summary,
        String probableRootCauseService,
        Double confidenceScore,
        int totalEvents,
        Set<String> affectedServices,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        List<EvidenceResponse> evidence
) {
}