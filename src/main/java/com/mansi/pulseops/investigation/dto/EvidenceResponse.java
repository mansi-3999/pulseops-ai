package com.mansi.pulseops.investigation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EvidenceResponse(
        UUID id,
        UUID eventId,
        String serviceName,
        String evidenceType,
        String description,
        double score,
        OffsetDateTime observedAt
) {
}