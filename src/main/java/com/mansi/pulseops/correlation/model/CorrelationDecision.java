/**
 * Represents the outcome of a correlation strategy,
 * including whether an incident should be created.
 */

package com.mansi.pulseops.correlation.model;

import com.mansi.pulseops.telemetry.domain.TelemetryEvent;

import java.util.List;

public record CorrelationDecision(

        boolean correlated,

        String correlationKey,

        String reason,

        List<TelemetryEvent> events
) {

    public static CorrelationDecision noMatch(
            String reason
    ) {
        return new CorrelationDecision(
                false,
                null,
                reason,
                List.of()
        );
    }

    public static CorrelationDecision match(
            String correlationKey,
            String reason,
            List<TelemetryEvent> events
    ) {
        return new CorrelationDecision(
                true,
                correlationKey,
                reason,
                List.copyOf(events)
        );
    }
}