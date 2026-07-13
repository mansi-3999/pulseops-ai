package com.mansi.pulseops.telemetry.dto;

import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record TelemetryEventRequest(

        @NotBlank
        @Size(max = 100)
        String serviceName,

        @NotBlank
        @Size(max = 100)
        String eventType,

        @NotNull
        TelemetrySeverity severity,

        @NotBlank
        @Size(max = 5000)
        String message,

        @Size(max = 150)
        String traceId,

        @NotNull
        @PastOrPresent
        OffsetDateTime occurredAt
) {
}