package com.mansi.pulseops.telemetry.dto;

import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TelemetryEventMessage(

        UUID eventId,
        String serviceName,
        String eventType,
        TelemetrySeverity severity,
        String message,
        String traceId,
        OffsetDateTime occurredAt,
        OffsetDateTime receivedAt
) {
}
