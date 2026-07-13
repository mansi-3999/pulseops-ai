/**
 * Represents evidence collected during deterministic
 * incident investigation.
 */

package com.mansi.pulseops.investigation.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "investigation_evidence")
public class InvestigationEvidence {

    @Id
    private UUID id;

    @Column(name = "investigation_id", nullable = false)
    private UUID investigationId;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "service_name", length = 255)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false, length = 50)
    private EvidenceType evidenceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double score;

    @Column(name = "observed_at")
    private OffsetDateTime observedAt;

    protected InvestigationEvidence() {
    }

    public static InvestigationEvidence create(
            UUID investigationId,
            UUID eventId,
            String serviceName,
            EvidenceType evidenceType,
            String description,
            double score,
            OffsetDateTime observedAt
    ) {
        InvestigationEvidence evidence =
                new InvestigationEvidence();

        evidence.id = UUID.randomUUID();
        evidence.investigationId = investigationId;
        evidence.eventId = eventId;
        evidence.serviceName = serviceName;
        evidence.evidenceType = evidenceType;
        evidence.description = description;
        evidence.score = score;
        evidence.observedAt = observedAt;

        return evidence;
    }

    public UUID getId() {
        return id;
    }

    public UUID getInvestigationId() {
        return investigationId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public String getDescription() {
        return description;
    }

    public double getScore() {
        return score;
    }

    public OffsetDateTime getObservedAt() {
        return observedAt;
    }
}