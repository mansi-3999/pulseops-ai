package com.mansi.pulseops.telemetry.kafka;

import com.mansi.pulseops.correlation.service.CorrelationService;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import com.mansi.pulseops.telemetry.dto.TelemetryEventMessage;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TelemetryConsumerTest {

    private final TelemetryEventRepository repository =
            mock(TelemetryEventRepository.class);

    private final CorrelationService correlationService =
            mock(CorrelationService.class);

    private final TelemetryConsumer consumer =
            new TelemetryConsumer(
                    repository,
                    correlationService
            );

    @Test
    void shouldPersistAndCorrelateNewTelemetryEvent() {

        UUID eventId =
                UUID.randomUUID();

        TelemetryEventMessage message =
                new TelemetryEventMessage(
                        eventId,
                        "payment-service",
                        "DATABASE_TIMEOUT",
                        TelemetrySeverity.ERROR,
                        "Database timeout",
                        "trace-123",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(repository.existsById(eventId))
                .thenReturn(false);

        when(repository.saveAndFlush(
                any(TelemetryEvent.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        consumer.consume(message);

        verify(repository)
                .saveAndFlush(
                        any(TelemetryEvent.class)
                );

        verify(correlationService)
                .correlate(
                        any(TelemetryEvent.class)
                );
    }

    @Test
    void shouldIgnoreDuplicateTelemetryEvent() {

        UUID eventId =
                UUID.randomUUID();

        TelemetryEventMessage message =
                new TelemetryEventMessage(
                        eventId,
                        "payment-service",
                        "DATABASE_TIMEOUT",
                        TelemetrySeverity.ERROR,
                        "Database timeout",
                        "trace-123",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        when(repository.existsById(eventId))
                .thenReturn(true);

        consumer.consume(message);

        verify(repository, never())
                .saveAndFlush(
                        any(TelemetryEvent.class)
                );

        verifyNoInteractions(
                correlationService
        );
    }
}