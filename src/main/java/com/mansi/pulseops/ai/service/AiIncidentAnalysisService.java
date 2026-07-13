/**
 * Generates AI-assisted incident investigations using
 * Amazon Bedrock and deterministic investigation results.
 */

package com.mansi.pulseops.ai.service;

import com.mansi.pulseops.ai.client.AiClient;
import com.mansi.pulseops.ai.config.AiProperties;
import com.mansi.pulseops.ai.domain.AiAnalysis;
import com.mansi.pulseops.ai.dto.AiAnalysisResponse;
import com.mansi.pulseops.ai.prompt.IncidentPromptBuilder;
import com.mansi.pulseops.ai.repository.AiAnalysisRepository;
import com.mansi.pulseops.investigation.dto.InvestigationResponse;
import com.mansi.pulseops.investigation.service.InvestigationService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AiIncidentAnalysisService {

    private final AiAnalysisRepository repository;
    private final InvestigationService investigationService;
    private final IncidentPromptBuilder promptBuilder;
    private final AiClient aiClient;
    private final AiProperties properties;
    private final Counter aiRequestsCounter;

    public AiIncidentAnalysisService(
            AiAnalysisRepository repository,
            InvestigationService investigationService,
            IncidentPromptBuilder promptBuilder,
            AiClient aiClient,
            AiProperties properties,
            MeterRegistry meterRegistry
    ) {
        this.repository = repository;
        this.investigationService = investigationService;
        this.promptBuilder = promptBuilder;
        this.aiClient = aiClient;
        this.properties = properties;

        this.aiRequestsCounter =
                Counter.builder("pulseops.ai.requests")
                        .description("Total AI analysis requests")
                        .register(meterRegistry);
    }

    public AiAnalysisResponse analyzeIncident(
            UUID incidentId
    ) {

        if (!properties.isEnabled()) {
            throw new IllegalStateException(
                    "AI analysis is disabled. " +
                            "Set PULSEOPS_AI_ENABLED=true."
            );
        }

        InvestigationResponse investigation =
                investigationService
                        .getLatestByIncident(incidentId);

        AiAnalysis analysis =
                AiAnalysis.start(
                        incidentId,
                        investigation.id(),
                        properties.getModelId(),
                        IncidentPromptBuilder.PROMPT_VERSION
                );

        analysis = repository.save(analysis);

        try {
            String prompt =
                    promptBuilder.build(investigation);

            String aiResponse =
                    aiClient.analyze(prompt);

            aiRequestsCounter.increment();

            analysis.complete(aiResponse);

            AiAnalysis saved =
                    repository.save(analysis);

            return toResponse(saved);

        } catch (Exception exception) {

            analysis.fail(
                    safeMessage(exception)
            );

            repository.save(analysis);

            throw exception;
        }
    }

    public AiAnalysisResponse getLatestByIncident(
            UUID incidentId
    ) {
        AiAnalysis analysis =
                repository
                        .findTopByIncidentIdOrderByCreatedAtDesc(
                                incidentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "AI analysis not found for incident: "
                                                + incidentId
                                )
                        );

        return toResponse(analysis);
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }

        return message.length() > 2000
                ? message.substring(0, 2000)
                : message;
    }

    private AiAnalysisResponse toResponse(
            AiAnalysis analysis
    ) {
        return new AiAnalysisResponse(
                analysis.getId(),
                analysis.getIncidentId(),
                analysis.getInvestigationId(),
                analysis.getStatus().name(),
                analysis.getModelId(),
                analysis.getPromptVersion(),
                analysis.getAnalysisText(),
                analysis.getErrorMessage(),
                analysis.getCreatedAt(),
                analysis.getCompletedAt()
        );
    }
}