package com.mansi.pulseops.incident.domain;

import jakarta.persistence.*;
import org.hibernate.query.sqm.mutation.internal.cte.CteInsertStrategy;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentStatus status;

    @Column(name = "detected_at", nullable = false)
    private OffsetDateTime detectedAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "correlation_key", length = 255)
    private String correlationKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 50)
    private IncidentSource source = IncidentSource.MANUAL;

    @Column(name = "event_count", nullable = false)
    private int eventCount = 0;

    @Column(name = "last_event_at")
    private OffsetDateTime lastEventAt;

    protected Incident() {
    }

    public Incident(
            UUID id,
            String title,
            String description,
            Severity severity,
            IncidentStatus status,
            OffsetDateTime detectedAt,
            OffsetDateTime resolvedAt,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.detectedAt = detectedAt;
        this.resolvedAt = resolvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Incident correlated(
            UUID id,
            String title,
            String description,
            Severity severity,
            OffsetDateTime detectedAt,
            String correlationKey,
            int eventCount,
            OffsetDateTime lastEventAt
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        Incident incident = new Incident();

        incident.id = id;
        incident.title = title;
        incident.description = description;
        incident.severity = severity;
        incident.status = IncidentStatus.OPEN;
        incident.detectedAt = detectedAt;
        incident.resolvedAt = null;

        incident.createdAt = now;
        incident.updatedAt = now;

        incident.correlationKey = correlationKey;
        incident.source = IncidentSource.CORRELATION_ENGINE;
        incident.eventCount = eventCount;
        incident.lastEventAt = lastEventAt;

        return incident;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getDetectedAt() {
        return detectedAt;
    }

    public OffsetDateTime getResolvedAt() {
        return resolvedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCorrelationKey() {
        return correlationKey;
    }

    public IncidentSource getSource() {
        return source;
    }

    public int getEventCount() {
        return eventCount;
    }

    public OffsetDateTime getLastEventAt() {
        return lastEventAt;
    }

    public void updateStatus(IncidentStatus status) {
        this.status = status;
        this.updatedAt = OffsetDateTime.now();

        if (status == IncidentStatus.RESOLVED
                || status == IncidentStatus.CLOSED) {
            this.resolvedAt = OffsetDateTime.now();
        }
    }

    public void updateCorrelationStats(
            int eventCount,
            OffsetDateTime lastEventAt
    ) {
        this.eventCount = eventCount;
        this.lastEventAt = lastEventAt;
        this.updatedAt = OffsetDateTime.now();
    }
}