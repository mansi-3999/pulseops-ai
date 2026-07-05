package com.mansi.pulseops.telemetry.kafka;

import com.mansi.pulseops.correlation.service.CorrelationService;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.dto.TelemetryEventMessage;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TelemetryConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TelemetryConsumer.class
            );

    private final TelemetryEventRepository repository;
    private final CorrelationService correlationService;

    public TelemetryConsumer(
            TelemetryEventRepository repository,
            CorrelationService correlationService
    ) {
        this.repository = repository;
        this.correlationService = correlationService;
    }

    @KafkaListener(
            topics = "${pulseops.kafka.topics.telemetry-raw}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consume(
            TelemetryEventMessage message
    ) {
        log.info(
                "Received telemetry event. eventId={}, serviceName={}, traceId={}",
                message.eventId(),
                message.serviceName(),
                message.traceId()
        );

        if (repository.existsById(
                message.eventId()
        )) {
            log.warn(
                    "Duplicate telemetry event ignored. eventId={}",
                    message.eventId()
            );

            return;
        }

        TelemetryEvent event =
                new TelemetryEvent(
                        message.eventId(),
                        message.serviceName(),
                        message.eventType(),
                        message.severity(),
                        message.message(),
                        message.traceId(),
                        message.occurredAt(),
                        message.receivedAt(),
                        null
                );

        TelemetryEvent savedEvent =
                repository.saveAndFlush(event);

        log.info(
                "Persisted telemetry event. eventId={}",
                savedEvent.getId()
        );

        correlationService.correlate(
                savedEvent
        );
    }
}