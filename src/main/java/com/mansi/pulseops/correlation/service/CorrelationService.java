/**
 * Correlates telemetry events into incidents using
 * configurable correlation strategies and temporal analysis.
 */

package com.mansi.pulseops.correlation.service;

import com.mansi.pulseops.correlation.model.CorrelationDecision;
import com.mansi.pulseops.correlation.strategy.CorrelationStrategy;
import com.mansi.pulseops.incident.domain.Incident;
import com.mansi.pulseops.incident.domain.IncidentStatus;
import com.mansi.pulseops.incident.domain.Severity;
import com.mansi.pulseops.incident.repository.IncidentRepository;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import com.mansi.pulseops.telemetry.repository.TelemetryEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CorrelationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CorrelationService.class
            );

    private final List<CorrelationStrategy> strategies;
    private final IncidentRepository incidentRepository;
    private final TelemetryEventRepository telemetryRepository;
    private final SeverityMapper severityMapper;

    public CorrelationService(
            List<CorrelationStrategy> strategies,
            IncidentRepository incidentRepository,
            TelemetryEventRepository telemetryRepository,
            SeverityMapper severityMapper
    ) {
        this.strategies = strategies;
        this.incidentRepository = incidentRepository;
        this.telemetryRepository = telemetryRepository;
        this.severityMapper = severityMapper;
    }

    @Transactional
    public void correlate(
            TelemetryEvent triggeringEvent
    ) {
        for (CorrelationStrategy strategy : strategies) {

            CorrelationDecision decision =
                    strategy.evaluate(triggeringEvent);

            if (!decision.correlated()) {
                continue;
            }

            processDecision(decision);
            return;
        }

        log.debug(
                "No correlation match for telemetry event. eventId={}",
                triggeringEvent.getId()
        );
    }

    private void processDecision(
            CorrelationDecision decision
    ) {
        Optional<Incident> existingIncident =
                incidentRepository
                        .findByCorrelationKeyAndStatusNot(
                                decision.correlationKey(),
                                IncidentStatus.RESOLVED
                        );

        if (existingIncident.isPresent()) {
            updateExistingIncident(
                    existingIncident.get(),
                    decision
            );
            return;
        }

        createNewIncident(decision);
    }

    private void createNewIncident(
            CorrelationDecision decision
    ) {
        List<TelemetryEvent> events =
                decision.events();

        Severity severity =
                severityMapper.derive(events);

        OffsetDateTime detectedAt =
                events.stream()
                        .map(TelemetryEvent::getOccurredAt)
                        .min(Comparator.naturalOrder())
                        .orElse(OffsetDateTime.now());

        OffsetDateTime lastEventAt =
                events.stream()
                        .map(TelemetryEvent::getOccurredAt)
                        .max(Comparator.naturalOrder())
                        .orElse(detectedAt);

        TelemetryEvent firstEvent =
                events.get(0);

        Incident incident =
                Incident.correlated(
                        UUID.randomUUID(),
                        buildTitle(firstEvent, events),
                        buildDescription(
                                decision,
                                events
                        ),
                        severity,
                        detectedAt,
                        decision.correlationKey(),
                        events.size(),
                        lastEventAt
                );

        Incident savedIncident =
                incidentRepository.save(incident);

        assignEvents(
                events,
                savedIncident.getId()
        );

        log.info(
                "Created correlated incident. incidentId={}, correlationKey={}, eventCount={}",
                savedIncident.getId(),
                decision.correlationKey(),
                events.size()
        );
    }

    private void updateExistingIncident(
            Incident incident,
            CorrelationDecision decision
    ) {
        List<TelemetryEvent> events =
                decision.events();

        assignEvents(
                events,
                incident.getId()
        );

        OffsetDateTime lastEventAt =
                events.stream()
                        .map(TelemetryEvent::getOccurredAt)
                        .max(Comparator.naturalOrder())
                        .orElse(OffsetDateTime.now());

        incident.updateCorrelationStats(
                events.size(),
                lastEventAt
        );

        incidentRepository.save(incident);

        log.info(
                "Updated correlated incident. incidentId={}, correlationKey={}, eventCount={}",
                incident.getId(),
                decision.correlationKey(),
                events.size()
        );
    }

    private void assignEvents(
            List<TelemetryEvent> events,
            UUID incidentId
    ) {
        events.forEach(event ->
                event.assignToIncident(incidentId)
        );

        telemetryRepository.saveAll(events);
    }

    private String buildTitle(
            TelemetryEvent firstEvent,
            List<TelemetryEvent> events
    ) {
        return "Correlated failure affecting "
                + firstEvent.getServiceName()
                + " and related services ("
                + events.size()
                + " events)";
    }

    private String buildDescription(
            CorrelationDecision decision,
            List<TelemetryEvent> events
    ) {
        String services =
                events.stream()
                        .map(TelemetryEvent::getServiceName)
                        .distinct()
                        .sorted()
                        .reduce(
                                (left, right) ->
                                        left + ", " + right
                        )
                        .orElse("unknown");

        return decision.reason()
                + ". Affected services: "
                + services;
    }
}