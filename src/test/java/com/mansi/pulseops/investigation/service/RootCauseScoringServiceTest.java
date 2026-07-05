package com.mansi.pulseops.investigation.service;

import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RootCauseScoringServiceTest {

    private final RootCauseScoringService service =
            new RootCauseScoringService();

    @Test
    void shouldReturnEmptyResultWhenNoEventsExist() {

        RootCauseScoringService.RootCauseResult result =
                service.calculate(List.of());

        assertNull(result.probableService());
        assertEquals(0.0, result.confidence());
        assertTrue(result.serviceScores().isEmpty());
    }

    @Test
    void shouldIdentifyEarliestCriticalServiceAsProbableRootCause() {

        OffsetDateTime baseTime =
                OffsetDateTime.parse("2026-07-05T17:50:00Z");

        TelemetryEvent paymentEvent =
                new TelemetryEvent(
                        UUID.randomUUID(),
                        "payment-service",
                        "DATABASE_TIMEOUT",
                        TelemetrySeverity.CRITICAL,
                        "Database connection pool exhausted",
                        "trace-test-001",
                        baseTime,
                        baseTime.plusSeconds(1),
                        null
                );

        TelemetryEvent orderEvent =
                new TelemetryEvent(
                        UUID.randomUUID(),
                        "order-service",
                        "PAYMENT_TIMEOUT",
                        TelemetrySeverity.ERROR,
                        "Payment authorization timed out",
                        "trace-test-001",
                        baseTime.plusSeconds(3),
                        baseTime.plusSeconds(4),
                        null
                );

        TelemetryEvent notificationEvent =
                new TelemetryEvent(
                        UUID.randomUUID(),
                        "notification-service",
                        "CONFIRMATION_MISSING",
                        TelemetrySeverity.WARN,
                        "Order confirmation event missing",
                        "trace-test-001",
                        baseTime.plusSeconds(6),
                        baseTime.plusSeconds(7),
                        null
                );

        RootCauseScoringService.RootCauseResult result =
                service.calculate(
                        List.of(
                                paymentEvent,
                                orderEvent,
                                notificationEvent
                        )
                );

        assertEquals(
                "payment-service",
                result.probableService()
        );

        assertTrue(
                result.confidence() > 0.0
        );

        assertFalse(
                result.serviceScores().isEmpty()
        );

        assertTrue(
                result.serviceScores()
                        .get("payment-service")
                        >
                        result.serviceScores()
                                .get("order-service")
        );
    }

    @Test
    void shouldGiveHigherScoreToRepeatedFailures() {

        OffsetDateTime baseTime =
                OffsetDateTime.parse("2026-07-05T18:00:00Z");

        TelemetryEvent paymentEvent1 =
                new TelemetryEvent(
                        UUID.randomUUID(),
                        "payment-service",
                        "DATABASE_TIMEOUT",
                        TelemetrySeverity.ERROR,
                        "Database timeout",
                        "trace-test-002",
                        baseTime,
                        baseTime.plusSeconds(1),
                        null
                );

        TelemetryEvent paymentEvent2 =
                new TelemetryEvent(
                        UUID.randomUUID(),
                        "payment-service",
                        "DATABASE_TIMEOUT",
                        TelemetrySeverity.ERROR,
                        "Database timeout repeated",
                        "trace-test-002",
                        baseTime.plusSeconds(2),
                        baseTime.plusSeconds(3),
                        null
                );

        TelemetryEvent orderEvent =
                new TelemetryEvent(
                        UUID.randomUUID(),
                        "order-service",
                        "PAYMENT_TIMEOUT",
                        TelemetrySeverity.ERROR,
                        "Payment request timed out",
                        "trace-test-002",
                        baseTime.plusSeconds(4),
                        baseTime.plusSeconds(5),
                        null
                );

        RootCauseScoringService.RootCauseResult result =
                service.calculate(
                        List.of(
                                paymentEvent1,
                                paymentEvent2,
                                orderEvent
                        )
                );

        assertEquals(
                "payment-service",
                result.probableService()
        );

        assertTrue(
                result.serviceScores()
                        .get("payment-service")
                        >
                        result.serviceScores()
                                .get("order-service")
        );
    }
}