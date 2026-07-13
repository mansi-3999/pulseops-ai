/**
 * Represents a completed investigation containing
 * root cause analysis and supporting evidence.
 */

package com.mansi.pulseops.investigation.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "investigations")
public class Investigation {

    @Id
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvestigationStatus status;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "probable_root_cause_service", length = 255)
    private String probableRootCauseService;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "total_events", nullable = false)
    private int totalEvents;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "investigation_services",
            joinColumns = @JoinColumn(name = "investigation_id")
    )
    @Column(name = "service_name", nullable = false)
    private Set<String> affectedServices = new LinkedHashSet<>();

    protected Investigation() {
    }

    public static Investigation start(
            UUID incidentId,
            int totalEvents
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        Investigation investigation = new Investigation();

        investigation.id = UUID.randomUUID();
        investigation.incidentId = incidentId;
        investigation.status = InvestigationStatus.RUNNING;
        investigation.totalEvents = totalEvents;
        investigation.startedAt = now;
        investigation.createdAt = now;
        investigation.updatedAt = now;

        return investigation;
    }

    public void complete(
            String summary,
            String probableRootCauseService,
            double confidenceScore,
            Set<String> affectedServices
    ) {
        this.summary = summary;
        this.probableRootCauseService = probableRootCauseService;
        this.confidenceScore = confidenceScore;
        this.affectedServices.clear();
        this.affectedServices.addAll(affectedServices);

        this.status = InvestigationStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void fail(String summary) {
        this.summary = summary;
        this.status = InvestigationStatus.FAILED;
        this.completedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getIncidentId() {
        return incidentId;
    }

    public InvestigationStatus getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }

    public String getProbableRootCauseService() {
        return probableRootCauseService;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public int getTotalEvents() {
        return totalEvents;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getAffectedServices() {
        return affectedServices;
    }
}