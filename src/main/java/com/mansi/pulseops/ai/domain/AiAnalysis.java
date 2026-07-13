/**
 * Represents a persisted AI-generated incident analysis
 * and its execution metadata.
 */

package com.mansi.pulseops.ai.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_analyses")
public class AiAnalysis {

    @Id
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "investigation_id", nullable = false)
    private UUID investigationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AiAnalysisStatus status;

    @Column(name = "model_id", length = 255)
    private String modelId;

    @Column(name = "prompt_version", nullable = false, length = 50)
    private String promptVersion;

    @Column(name = "analysis_text", columnDefinition = "TEXT")
    private String analysisText;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    protected AiAnalysis() {
    }

    public static AiAnalysis start(
            UUID incidentId,
            UUID investigationId,
            String modelId,
            String promptVersion
    ) {
        AiAnalysis analysis = new AiAnalysis();

        analysis.id = UUID.randomUUID();
        analysis.incidentId = incidentId;
        analysis.investigationId = investigationId;
        analysis.status = AiAnalysisStatus.RUNNING;
        analysis.modelId = modelId;
        analysis.promptVersion = promptVersion;
        analysis.createdAt = OffsetDateTime.now();

        return analysis;
    }

    public void complete(String analysisText) {
        this.analysisText = analysisText;
        this.status = AiAnalysisStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void fail(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = AiAnalysisStatus.FAILED;
        this.completedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getIncidentId() {
        return incidentId;
    }

    public UUID getInvestigationId() {
        return investigationId;
    }

    public AiAnalysisStatus getStatus() {
        return status;
    }

    public String getModelId() {
        return modelId;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String getAnalysisText() {
        return analysisText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }
}