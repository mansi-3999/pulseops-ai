package com.mansi.pulseops.correlation.strategy;

import com.mansi.pulseops.correlation.config.CorrelationProperties;
import com.mansi.pulseops.correlation.model.CorrelationDecision;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraceIdCorrelationStrategy
        implements CorrelationStrategy {

    private final TelemetryEventRepository repository;
    private final CorrelationProperties properties;

    public TraceIdCorrelationStrategy(
            TelemetryEventRepository repository,
            CorrelationProperties properties
    ) {
        this.repository = repository;
        this.properties = properties;
    }

    @Override
    public CorrelationDecision evaluate(
            TelemetryEvent event
    ) {
        String traceId = event.getTraceId();

        if (traceId == null || traceId.isBlank()) {
            return CorrelationDecision.noMatch(
                    "Telemetry event has no traceId"
            );
        }

        List<TelemetryEvent> relatedEvents =
                repository
                        .findByTraceIdOrderByOccurredAtAsc(
                                traceId
                        );

        if (relatedEvents.size()
                < properties.getMinimumEvents()) {

            return CorrelationDecision.noMatch(
                    "Insufficient related events for traceId "
                            + traceId
            );
        }

        return CorrelationDecision.match(
                "TRACE:" + traceId,
                "Matched "
                        + relatedEvents.size()
                        + " events using traceId "
                        + traceId,
                relatedEvents
        );
    }
}