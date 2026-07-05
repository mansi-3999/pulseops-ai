package com.mansi.pulseops.investigation.repository;

import com.mansi.pulseops.investigation.domain.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvestigationRepository
        extends JpaRepository<Investigation, UUID> {

    Optional<Investigation> findTopByIncidentIdOrderByCreatedAtDesc(
            UUID incidentId
    );
}