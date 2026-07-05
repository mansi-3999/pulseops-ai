package com.mansi.pulseops.telemetry.dto;

import java.util.UUID;

public record TelemetryAcceptedResponse(
        UUID eventId,
        String status
) {
}