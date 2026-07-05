package com.mansi.pulseops.incident.dto;

import com.mansi.pulseops.incident.domain.*;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IncidentResponse(UUID id, String title, String description, Severity severity, IncidentStatus status,
                               OffsetDateTime detectedAt, OffsetDateTime resolvedAt, OffsetDateTime createdAt,
                               OffsetDateTime updatedAt, String correlationKey,
                               IncidentSource source,
                               int eventCount,
                               OffsetDateTime lastEventAt) {
}
