package com.mansi.pulseops.telemetry.kafka;

import com.mansi.pulseops.telemetry.dto.TelemetryEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TelemetryProducer {

    private static final Logger log =
            LoggerFactory.getLogger(TelemetryProducer.class);

    private final KafkaTemplate<String, TelemetryEventMessage> kafkaTemplate;
    private final String topicName;

    public TelemetryProducer(
            KafkaTemplate<String, TelemetryEventMessage> kafkaTemplate,
            @Value("${pulseops.kafka.topics.telemetry-raw}")
            String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publish(TelemetryEventMessage message) {

        String key = message.traceId() != null
                && !message.traceId().isBlank()
                ? message.traceId()
                : message.eventId().toString();

        kafkaTemplate.send(topicName, key, message)
                .whenComplete((result, exception) -> {

                    if (exception != null) {
                        log.error(
                                "Failed to publish telemetry event. eventId={}, topic={}",
                                message.eventId(),
                                topicName,
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Published telemetry event. eventId={}, topic={}, partition={}, offset={}",
                            message.eventId(),
                            topicName,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }
}