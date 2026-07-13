package com.mansi.pulseops.telemetry.dto;

import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

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
