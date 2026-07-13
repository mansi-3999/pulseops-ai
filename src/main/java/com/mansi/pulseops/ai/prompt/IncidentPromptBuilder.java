/**
 * Builds structured prompts for large language models
 * using deterministic investigation results.
 */

package com.mansi.pulseops.ai.prompt;

import com.mansi.pulseops.investigation.dto.EvidenceResponse;
import com.mansi.pulseops.investigation.dto.InvestigationResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class IncidentPromptBuilder {

    public static final String PROMPT_VERSION = "v1";

    public String build(
            InvestigationResponse investigation
    ) {
        String evidenceText =
                investigation.evidence()
                        .stream()
                        .map(this::formatEvidence)
                        .collect(Collectors.joining("\n"));

        return """
                You are PulseOps AI, a production incident analysis assistant.
                
                Analyze only the evidence provided below.
                
                Rules:
                1. Do not invent services, events, dependencies, metrics, or causes.
                2. Clearly separate observed evidence from inference.
                3. Treat the deterministic root-cause candidate as a signal, not absolute truth.
                4. If evidence is insufficient, explicitly say so.
                5. Recommendations must be actionable and technically specific.
                6. Do not claim certainty unsupported by evidence.
                
                INCIDENT INVESTIGATION CONTEXT
                
                Incident ID:
                %s
                
                Deterministic Summary:
                %s
                
                Deterministic Root Cause Candidate:
                %s
                
                Deterministic Confidence:
                %s
                
                Total Correlated Events:
                %d
                
                Affected Services:
                %s
                
                Evidence:
                %s
                
                Produce the response using exactly these sections:
                
                ## Executive Summary
                
                ## Probable Root Cause
                
                ## Failure Chain
                
                ## Business Impact
                
                ## Recommended Actions
                
                ## Confidence and Caveats
                """
                .formatted(
                        investigation.incidentId(),
                        investigation.summary(),
                        investigation.probableRootCauseService(),
                        investigation.confidenceScore(),
                        investigation.totalEvents(),
                        investigation.affectedServices(),
                        evidenceText.isBlank()
                                ? "No evidence available"
                                : evidenceText
                );
    }

    private String formatEvidence(
            EvidenceResponse evidence
    ) {
        return "- Type=%s | Service=%s | Description=%s | Score=%.2f | ObservedAt=%s"
                .formatted(
                        evidence.evidenceType(),
                        evidence.serviceName(),
                        evidence.description(),
                        evidence.score(),
                        evidence.observedAt()
                );
    }
}