package com.mansi.pulseops.config;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    public CommonErrorHandler telemetryErrorHandler(
            KafkaOperations<Object, Object> kafkaOperations
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaOperations,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                );

        FixedBackOff backOff =
                new FixedBackOff(
                        1000L,
                        3L
                );

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        recoverer,
                        backOff
                );

        errorHandler.setRetryListeners(
                (record, exception, deliveryAttempt) ->
                        log.warn(
                                "Retrying telemetry event. topic={}, partition={}, offset={}, attempt={}",
                                record.topic(),
                                record.partition(),
                                record.offset(),
                                deliveryAttempt,
                                exception
                        )
        );

        return errorHandler;
    }
}