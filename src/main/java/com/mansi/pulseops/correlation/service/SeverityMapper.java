package com.mansi.pulseops.correlation.service;

import com.mansi.pulseops.incident.domain.Severity;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeverityMapper {

    public Severity derive(
            List<TelemetryEvent> events
    ) {
        boolean hasCritical =
                events.stream()
                        .anyMatch(event ->
                                event.getSeverity()
                                        == TelemetrySeverity.CRITICAL
                        );

        if (hasCritical) {
            return Severity.CRITICAL;
        }

        long errorCount =
                events.stream()
                        .filter(event ->
                                event.getSeverity()
                                        == TelemetrySeverity.ERROR
                        )
                        .count();

        if (errorCount >= 3) {
            return Severity.CRITICAL;
        }

        if (errorCount >= 1) {
            return Severity.HIGH;
        }

        boolean hasWarn =
                events.stream()
                        .anyMatch(event ->
                                event.getSeverity()
                                        == TelemetrySeverity.WARN
                        );

        if (hasWarn) {
            return Severity.MEDIUM;
        }

        return Severity.LOW;
    }
}