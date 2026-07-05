package com.mansi.pulseops.telemetry.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "telemetry_events")
public class TelemetryEvent {

    @Id
    private UUID id;

    @Column(
            name = "service_name",
            nullable = false,
            length = 100
    )
    private String serviceName;

    @Column(
            name = "event_type",
            nullable = false,
            length = 100
    )
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "severity",
            nullable = false,
            length = 20
    )
    private TelemetrySeverity severity;

    @Column(
            name = "message",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String message;

    @Column(
            name = "trace_id",
            length = 150
    )
    private String traceId;

    @Column(
            name = "occurred_at",
            nullable = false
    )
    private OffsetDateTime occurredAt;

    @Column(
            name = "received_at",
            nullable = false
    )
    private OffsetDateTime receivedAt;

    @Column(name = "incident_id")
    private UUID incidentId;

    protected TelemetryEvent() {
        // Required by JPA
    }

    public TelemetryEvent(
            UUID id,
            String serviceName,
            String eventType,
            TelemetrySeverity severity,
            String message,
            String traceId,
            OffsetDateTime occurredAt,
            OffsetDateTime receivedAt,
            UUID incidentId
    ) {
        this.id = id;
        this.serviceName = serviceName;
        this.eventType = eventType;
        this.severity = severity;
        this.message = message;
        this.traceId = traceId;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
        this.incidentId = incidentId;
    }

    public UUID getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEventType() {
        return eventType;
    }

    public TelemetrySeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getTraceId() {
        return traceId;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

    public UUID getIncidentId() {
        return incidentId;
    }

    public void assignToIncident(UUID incidentId) {
        this.incidentId = incidentId;
    }
}