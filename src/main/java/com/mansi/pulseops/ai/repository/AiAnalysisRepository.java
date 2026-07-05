package com.mansi.pulseops.ai.repository;

import com.mansi.pulseops.ai.domain.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiAnalysisRepository
        extends JpaRepository<AiAnalysis, UUID> {

    Optional<AiAnalysis>
    findTopByIncidentIdOrderByCreatedAtDesc(
            UUID incidentId
    );
}