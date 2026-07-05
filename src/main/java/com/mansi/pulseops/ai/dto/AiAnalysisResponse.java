package com.mansi.pulseops.ai.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AiAnalysisResponse(
        UUID id,
        UUID incidentId,
        UUID investigationId,
        String status,
        String modelId,
        String promptVersion,
        String analysisText,
        String errorMessage,
        OffsetDateTime createdAt,
        OffsetDateTime completedAt
) {
}