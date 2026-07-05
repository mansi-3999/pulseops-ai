package com.mansi.pulseops.correlation.service;

import com.mansi.pulseops.incident.domain.Severity;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SeverityMapperTest {

    private final SeverityMapper mapper =
            new SeverityMapper();

    @Test
    void shouldMapCriticalTelemetryToCriticalIncident() {

        var result =
                mapper.derive(
                        List.of(
                                event(
                                        TelemetrySeverity.CRITICAL
                                )
                        )
                );

        assertThat(result)
                .isEqualTo(
                        Severity.CRITICAL
                );
    }

    @Test
    void shouldMapErrorToHighIncident() {

        var result =
                mapper.derive(
                        List.of(
                                event(
                                        TelemetrySeverity.ERROR
                                )
                        )
                );

        assertThat(result)
                .isEqualTo(
                        Severity.HIGH
                );
    }

    @Test
    void shouldMapThreeErrorsToCriticalIncident() {

        var result =
                mapper.derive(
                        List.of(
                                event(TelemetrySeverity.ERROR),
                                event(TelemetrySeverity.ERROR),
                                event(TelemetrySeverity.ERROR)
                        )
                );

        assertThat(result)
                .isEqualTo(
                        Severity.CRITICAL
                );
    }

    private TelemetryEvent event(
            TelemetrySeverity severity
    ) {
        return new TelemetryEvent(
                UUID.randomUUID(),
                "payment-service",
                "TEST_EVENT",
                severity,
                "Test",
                "trace-123",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null
        );
    }
}