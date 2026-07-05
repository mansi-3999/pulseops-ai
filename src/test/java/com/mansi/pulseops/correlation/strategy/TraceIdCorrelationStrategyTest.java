package com.mansi.pulseops.correlation.strategy;

import com.mansi.pulseops.correlation.config.CorrelationProperties;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TraceIdCorrelationStrategyTest {

    private final TelemetryEventRepository repository =
            mock(TelemetryEventRepository.class);

    private final CorrelationProperties properties =
            new CorrelationProperties();

    private final TraceIdCorrelationStrategy strategy =
            new TraceIdCorrelationStrategy(
                    repository,
                    properties
            );

    @Test
    void shouldCorrelateWhenMinimumEventsShareTraceId() {

        properties.setMinimumEvents(2);

        TelemetryEvent first =
                event(
                        "payment-service",
                        "trace-123"
                );

        TelemetryEvent second =
                event(
                        "order-service",
                        "trace-123"
                );

        when(repository
                .findByTraceIdOrderByOccurredAtAsc(
                        "trace-123"
                ))
                .thenReturn(
                        List.of(first, second)
                );

        var decision =
                strategy.evaluate(second);

        assertThat(decision.correlated())
                .isTrue();

        assertThat(decision.correlationKey())
                .isEqualTo(
                        "TRACE:trace-123"
                );

        assertThat(decision.events())
                .hasSize(2);
    }

    @Test
    void shouldNotCorrelateWithoutTraceId() {

        TelemetryEvent event =
                event(
                        "payment-service",
                        null
                );

        var decision =
                strategy.evaluate(event);

        assertThat(decision.correlated())
                .isFalse();

        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotCorrelateBelowThreshold() {

        properties.setMinimumEvents(2);

        TelemetryEvent event =
                event(
                        "payment-service",
                        "trace-single"
                );

        when(repository
                .findByTraceIdOrderByOccurredAtAsc(
                        "trace-single"
                ))
                .thenReturn(
                        List.of(event)
                );

        var decision =
                strategy.evaluate(event);

        assertThat(decision.correlated())
                .isFalse();
    }

    private TelemetryEvent event(
            String serviceName,
            String traceId
    ) {
        return new TelemetryEvent(
                UUID.randomUUID(),
                serviceName,
                "TEST_EVENT",
                TelemetrySeverity.ERROR,
                "Test failure",
                traceId,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null
        );
    }
}