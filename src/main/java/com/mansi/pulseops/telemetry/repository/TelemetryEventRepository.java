package com.mansi.pulseops.telemetry.repository;

import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.domain.TelemetrySeverity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TelemetryEventRepository
        extends JpaRepository<TelemetryEvent, UUID> {

    List<TelemetryEvent>
    findByTraceIdOrderByOccurredAtAsc(
            String traceId
    );

    List<TelemetryEvent>
    findByServiceNameOrderByOccurredAtDesc(
            String serviceName
    );

    List<TelemetryEvent>
    findBySeverityOrderByOccurredAtDesc(
            TelemetrySeverity severity
    );
    long countByIncidentId(UUID incidentId);
}