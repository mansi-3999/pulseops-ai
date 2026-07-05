package com.mansi.pulseops.ai.prompt;

import com.mansi.pulseops.investigation.dto.EvidenceResponse;
import com.mansi.pulseops.investigation.dto.InvestigationResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentPromptBuilderTest {

    private final IncidentPromptBuilder builder =
            new IncidentPromptBuilder();

    @Test
    void shouldBuildGroundedIncidentPrompt() {

        UUID incidentId = UUID.randomUUID();

        EvidenceResponse evidence =
                new EvidenceResponse(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "payment-service",
                        "EARLIEST_FAILURE",
                        "Earliest correlated failure observed",
                        4.0,
                        OffsetDateTime.parse(
                                "2026-07-05T18:20:01Z"
                        )
                );

        InvestigationResponse investigation =
                new InvestigationResponse(
                        UUID.randomUUID(),
                        incidentId,
                        "COMPLETED",
                        "Analyzed 3 correlated events",
                        "payment-service",
                        0.55,
                        3,
                        Set.of(
                                "payment-service",
                                "order-service"
                        ),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        List.of(evidence)
                );

        String prompt =
                builder.build(investigation);

        assertTrue(
                prompt.contains("payment-service")
        );

        assertTrue(
                prompt.contains("EARLIEST_FAILURE")
        );

        assertTrue(
                prompt.contains(
                        "Do not invent services"
                )
        );

        assertTrue(
                prompt.contains(
                        "Recommended Actions"
                )
        );
    }
}