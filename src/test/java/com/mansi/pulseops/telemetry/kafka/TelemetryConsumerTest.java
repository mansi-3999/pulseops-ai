package com.mansi.pulseops.telemetry.kafka;

import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import com.mansi.pulseops.telemetry.dto.TelemetryEventMessage;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TelemetryConsumerTest {

    private final TelemetryEventRepository repository =
            mock(TelemetryEventRepository.class);

    private final TelemetryConsumer consumer =
            new TelemetryConsumer(repository);

    @Test
    void shouldPersistNewTelemetryEvent() {

        UUID eventId = UUID.randomUUID();

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

        when(repository.save(any(TelemetryEvent.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        consumer.consume(message);

        ArgumentCaptor<TelemetryEvent> captor =
                ArgumentCaptor.forClass(
                        TelemetryEvent.class
                );

        verify(repository)
                .save(captor.capture());

        TelemetryEvent saved =
                captor.getValue();

        assertThat(saved.getId())
                .isEqualTo(eventId);

        assertThat(saved.getServiceName())
                .isEqualTo("payment-service");

        assertThat(saved.getTraceId())
                .isEqualTo("trace-123");

        assertThat(saved.getIncidentId())
                .isNull();
    }

    @Test
    void shouldIgnoreDuplicateTelemetryEvent() {

        UUID eventId = UUID.randomUUID();

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
                .save(any(TelemetryEvent.class));
    }
}