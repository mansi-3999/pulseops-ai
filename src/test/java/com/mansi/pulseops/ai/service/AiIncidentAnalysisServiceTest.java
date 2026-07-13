package com.mansi.pulseops.ai.service;

import com.mansi.pulseops.ai.client.AiClient;
import com.mansi.pulseops.ai.config.AiProperties;
import com.mansi.pulseops.ai.domain.AiAnalysis;
import com.mansi.pulseops.ai.dto.AiAnalysisResponse;
import com.mansi.pulseops.ai.prompt.IncidentPromptBuilder;
import com.mansi.pulseops.ai.repository.AiAnalysisRepository;
import com.mansi.pulseops.investigation.dto.InvestigationResponse;
import com.mansi.pulseops.investigation.service.InvestigationService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiIncidentAnalysisServiceTest {


    @Test
    void shouldPersistCompletedAiAnalysis() {

        AiAnalysisRepository repository =
                mock(AiAnalysisRepository.class);

        InvestigationService investigationService =
                mock(InvestigationService.class);

        IncidentPromptBuilder promptBuilder =
                mock(IncidentPromptBuilder.class);

        AiClient aiClient =
                mock(AiClient.class);

        AiProperties properties =
                new AiProperties();

        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

        properties.setEnabled(true);

        properties.setModelId(
                "test-model"
        );

        UUID incidentId =
                UUID.randomUUID();

        InvestigationResponse investigation =
                new InvestigationResponse(
                        UUID.randomUUID(),
                        incidentId,
                        "COMPLETED",
                        "Test summary",
                        "payment-service",
                        0.75,
                        3,
                        Set.of("payment-service"),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        List.of()
                );

        when(
                investigationService
                        .getLatestByIncident(incidentId)
        ).thenReturn(investigation);

        when(
                promptBuilder.build(investigation)
        ).thenReturn("grounded prompt");

        when(
                aiClient.analyze("grounded prompt")
        ).thenReturn(
                "AI incident analysis"
        );

        when(
                repository.save(any(AiAnalysis.class))
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        AiIncidentAnalysisService service =
                new AiIncidentAnalysisService(
                        repository,
                        investigationService,
                        promptBuilder,
                        aiClient,
                        properties,
                        meterRegistry
                );

        AiAnalysisResponse response =
                service.analyzeIncident(incidentId);

        assertEquals(
                "COMPLETED",
                response.status()
        );

        assertEquals(
                "AI incident analysis",
                response.analysisText()
        );

        verify(
                aiClient,
                times(1)
        ).analyze("grounded prompt");
    }
}