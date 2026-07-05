package com.mansi.pulseops.investigation.repository;

import com.mansi.pulseops.investigation.domain.InvestigationEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvestigationEvidenceRepository
        extends JpaRepository<InvestigationEvidence, UUID> {

    List<InvestigationEvidence>
    findByInvestigationIdOrderByObservedAtAsc(
            UUID investigationId
    );
}