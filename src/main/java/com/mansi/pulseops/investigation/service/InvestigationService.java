/**
 * Builds investigations by analyzing correlated telemetry
 * and identifying the most probable root cause.
 */

package com.mansi.pulseops.investigation.service;

import com.mansi.pulseops.incident.domain.Incident;
import com.mansi.pulseops.incident.repository.IncidentRepository;
import com.mansi.pulseops.investigation.domain.EvidenceType;
import com.mansi.pulseops.investigation.domain.Investigation;
import com.mansi.pulseops.investigation.domain.InvestigationEvidence;
import com.mansi.pulseops.investigation.dto.EvidenceResponse;
import com.mansi.pulseops.investigation.dto.InvestigationResponse;
import com.mansi.pulseops.investigation.repository.InvestigationEvidenceRepository;
import com.mansi.pulseops.investigation.repository.InvestigationRepository;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvestigationService {

    private final InvestigationRepository investigationRepository;
    private final InvestigationEvidenceRepository evidenceRepository;
    private final IncidentRepository incidentRepository;
    private final TelemetryEventRepository telemetryEventRepository;
    private final RootCauseScoringService rootCauseScoringService;

    public InvestigationService(
            InvestigationRepository investigationRepository,
            InvestigationEvidenceRepository evidenceRepository,
            IncidentRepository incidentRepository,
            TelemetryEventRepository telemetryEventRepository,
            RootCauseScoringService rootCauseScoringService
    ) {
        this.investigationRepository = investigationRepository;
        this.evidenceRepository = evidenceRepository;
        this.incidentRepository = incidentRepository;
        this.telemetryEventRepository = telemetryEventRepository;
        this.rootCauseScoringService = rootCauseScoringService;
    }

    @Transactional
    public InvestigationResponse investigate(
            UUID incidentId
    ) {
        Incident incident = incidentRepository
                .findById(incidentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Incident not found: " + incidentId
                        )
                );

        String correlationKey = incident.getCorrelationKey();

        if (correlationKey == null
                || !correlationKey.startsWith("TRACE:")) {
            throw new IllegalStateException(
                    "Incident does not contain a TRACE correlation key"
            );
        }

        String traceId = correlationKey.substring(
                "TRACE:".length()
        );

        List<TelemetryEvent> events =
                telemetryEventRepository
                        .findByTraceIdOrderByOccurredAtAsc(
                                traceId
                        );

        if (events.isEmpty()) {
            throw new IllegalStateException(
                    "No telemetry events found for traceId: "
                            + traceId
            );
        }

        Investigation investigation =
                Investigation.start(
                        incidentId,
                        events.size()
                );

        investigationRepository.save(investigation);

        RootCauseScoringService.RootCauseResult result =
                rootCauseScoringService.calculate(events);

        Set<String> affectedServices = events.stream()
                .map(TelemetryEvent::getServiceName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(
                        LinkedHashSet::new
                ));

        List<InvestigationEvidence> evidenceList =
                buildEvidence(
                        investigation.getId(),
                        events
                );

        evidenceRepository.saveAll(evidenceList);

        String summary = buildSummary(
                events,
                affectedServices,
                result
        );

        investigation.complete(
                summary,
                result.probableService(),
                result.confidence(),
                affectedServices
        );

        Investigation saved =
                investigationRepository.save(investigation);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InvestigationResponse getLatestByIncident(
            UUID incidentId
    ) {
        Investigation investigation =
                investigationRepository
                        .findTopByIncidentIdOrderByCreatedAtDesc(
                                incidentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Investigation not found for incident: "
                                                + incidentId
                                )
                        );

        return toResponse(investigation);
    }

    private List<InvestigationEvidence> buildEvidence(
            UUID investigationId,
            List<TelemetryEvent> events
    ) {
        List<InvestigationEvidence> evidence =
                new ArrayList<>();

        if (events.isEmpty()) {
            return evidence;
        }

        TelemetryEvent earliest = events.get(0);

        evidence.add(
                InvestigationEvidence.create(
                        investigationId,
                        earliest.getId(),
                        earliest.getServiceName(),
                        EvidenceType.EARLIEST_FAILURE,
                        "Earliest correlated failure observed in "
                                + earliest.getServiceName(),
                        4.0,
                        earliest.getOccurredAt()
                )
        );

        Map<String, Long> serviceCounts =
                events.stream()
                        .filter(e ->
                                e.getServiceName() != null
                        )
                        .collect(Collectors.groupingBy(
                                TelemetryEvent::getServiceName,
                                Collectors.counting()
                        ));

        for (TelemetryEvent event : events) {

            evidence.add(
                    InvestigationEvidence.create(
                            investigationId,
                            event.getId(),
                            event.getServiceName(),
                            EvidenceType.ERROR_EVENT,
                            "Correlated telemetry failure from "
                                    + event.getServiceName(),
                            1.0,
                            event.getOccurredAt()
                    )
            );

            long count = serviceCounts.getOrDefault(
                    event.getServiceName(),
                    0L
            );

            if (count > 1) {
                evidence.add(
                        InvestigationEvidence.create(
                                investigationId,
                                event.getId(),
                                event.getServiceName(),
                                EvidenceType.REPEATED_FAILURE,
                                "Repeated failures detected for "
                                        + event.getServiceName()
                                        + " count=" + count,
                                2.0,
                                event.getOccurredAt()
                        )
                );
            }
        }

        if (serviceCounts.size() > 1) {
            TelemetryEvent last = events.get(
                    events.size() - 1
            );

            evidence.add(
                    InvestigationEvidence.create(
                            investigationId,
                            last.getId(),
                            last.getServiceName(),
                            EvidenceType.CROSS_SERVICE_FAILURE,
                            "Failure propagated across "
                                    + serviceCounts.size()
                                    + " services",
                            3.0,
                            last.getOccurredAt()
                    )
            );
        }

        return evidence;
    }

    private String buildSummary(
            List<TelemetryEvent> events,
            Set<String> affectedServices,
            RootCauseScoringService.RootCauseResult result
    ) {
        return "Investigation analyzed "
                + events.size()
                + " correlated telemetry events across "
                + affectedServices.size()
                + " services. Probable root-cause candidate: "
                + result.probableService()
                + ".";
    }

    private InvestigationResponse toResponse(
            Investigation investigation
    ) {
        List<EvidenceResponse> evidence =
                evidenceRepository
                        .findByInvestigationIdOrderByObservedAtAsc(
                                investigation.getId()
                        )
                        .stream()
                        .map(e ->
                                new EvidenceResponse(
                                        e.getId(),
                                        e.getEventId(),
                                        e.getServiceName(),
                                        e.getEvidenceType().name(),
                                        e.getDescription(),
                                        e.getScore(),
                                        e.getObservedAt()
                                )
                        )
                        .toList();

        return new InvestigationResponse(
                investigation.getId(),
                investigation.getIncidentId(),
                investigation.getStatus().name(),
                investigation.getSummary(),
                investigation.getProbableRootCauseService(),
                investigation.getConfidenceScore(),
                investigation.getTotalEvents(),
                investigation.getAffectedServices(),
                investigation.getStartedAt(),
                investigation.getCompletedAt(),
                evidence
        );
    }
}