package com.mansi.pulseops.telemetry.service;

import com.mansi.pulseops.telemetry.dto.TelemetryAcceptedResponse;
import com.mansi.pulseops.telemetry.dto.TelemetryEventMessage;
import com.mansi.pulseops.telemetry.dto.TelemetryEventRequest;
import com.mansi.pulseops.telemetry.kafka.TelemetryProducer;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TelemetryService {

    private final TelemetryProducer producer;

    public TelemetryService(TelemetryProducer producer) {
        this.producer = producer;
    }

    public TelemetryAcceptedResponse ingest(
            TelemetryEventRequest request
    ) {
        UUID eventId = UUID.randomUUID();

        String normalizedTraceId =
                request.traceId() == null
                        || request.traceId().isBlank()
                        ? null
                        : request.traceId().trim();

        TelemetryEventMessage message =
                new TelemetryEventMessage(
                        eventId,
                        request.serviceName().trim(),
                        request.eventType().trim(),
                        request.severity(),
                        request.message().trim(),
                        normalizedTraceId,
                        request.occurredAt(),
                        OffsetDateTime.now()
                );

        producer.publish(message);

        return new TelemetryAcceptedResponse(
                eventId,
                "ACCEPTED"
        );
    }
}