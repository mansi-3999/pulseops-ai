package com.mansi.pulseops.investigation.service;

import com.mansi.pulseops.telemetry.domain.TelemetryEvent;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RootCauseScoringService {

    public RootCauseResult calculate(
            List<TelemetryEvent> events
    ) {
        if (events == null || events.isEmpty()) {
            return new RootCauseResult(
                    null,
                    0.0,
                    Map.of()
            );
        }

        Map<String, Double> scores = new HashMap<>();

        for (int i = 0; i < events.size(); i++) {

            TelemetryEvent event = events.get(i);

            String serviceName = event.getServiceName();

            if (serviceName == null || serviceName.isBlank()) {
                continue;
            }

            double score = 0.0;

            // Earliest event gets stronger root-cause weight
            if (i == 0) {
                score += 4.0;
            }

            // Every observed failure contributes
            score += 1.0;

            // Repeated failures increase suspicion
            long occurrences = events.stream()
                    .filter(e ->
                            serviceName.equals(
                                    e.getServiceName()
                            )
                    )
                    .count();

            if (occurrences > 1) {
                score += occurrences * 1.5;
            }

            scores.merge(
                    serviceName,
                    score,
                    Double::sum
            );
        }

        Map.Entry<String, Double> winner =
                scores.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);

        if (winner == null) {
            return new RootCauseResult(
                    null,
                    0.0,
                    scores
            );
        }

        double totalScore = scores.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double confidence = totalScore == 0
                ? 0.0
                : winner.getValue() / totalScore;

        confidence = Math.min(
                0.99,
                Math.max(0.0, confidence)
        );

        return new RootCauseResult(
                winner.getKey(),
                confidence,
                scores
        );
    }

    public record RootCauseResult(
            String probableService,
            double confidence,
            Map<String, Double> serviceScores
    ) {
    }
}