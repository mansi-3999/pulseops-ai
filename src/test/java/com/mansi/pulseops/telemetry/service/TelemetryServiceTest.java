package com.mansi.pulseops.telemetry.service;

import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import com.mansi.pulseops.telemetry.dto.TelemetryEventMessage;
import com.mansi.pulseops.telemetry.dto.TelemetryEventRequest;
import com.mansi.pulseops.telemetry.kafka.TelemetryProducer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TelemetryServiceTest {

    private final TelemetryProducer producer =
            mock(TelemetryProducer.class);

    private final TelemetryService service =
            new TelemetryService(producer);

    @Test
    void shouldNormalizeAndPublishTelemetryEvent() {

        OffsetDateTime occurredAt =
                OffsetDateTime.parse(
                        "2026-07-05T13:20:01Z"
                );

        TelemetryEventRequest request =
                new TelemetryEventRequest(
                        " payment-service ",
                        " DATABASE_TIMEOUT ",
                        TelemetrySeverity.ERROR,
                        " Database timeout ",
                        " trace-123 ",
                        occurredAt
                );

        var response =
                service.ingest(request);

        ArgumentCaptor<TelemetryEventMessage> captor =
                ArgumentCaptor.forClass(
                        TelemetryEventMessage.class
                );

        verify(producer)
                .publish(captor.capture());

        TelemetryEventMessage published =
                captor.getValue();

        assertThat(response.status())
                .isEqualTo("ACCEPTED");

        assertThat(response.eventId())
                .isEqualTo(published.eventId());

        assertThat(published.serviceName())
                .isEqualTo("payment-service");

        assertThat(published.eventType())
                .isEqualTo("DATABASE_TIMEOUT");

        assertThat(published.message())
                .isEqualTo("Database timeout");

        assertThat(published.traceId())
                .isEqualTo("trace-123");

        assertThat(published.occurredAt())
                .isEqualTo(occurredAt);

        assertThat(published.receivedAt())
                .isNotNull();
    }
}