package com.mansi.pulseops.incident.repository;

import com.mansi.pulseops.incident.domain.Incident;
import com.mansi.pulseops.incident.domain.IncidentStatus;
import com.mansi.pulseops.incident.domain.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    List<Incident> findByStatusOrderByDetectedAtDesc(IncidentStatus status);

    List<Incident> findBySeverityOrderByDetectedAtDesc(Severity severity);

    Optional<Incident> findByCorrelationKeyAndStatusNot(
            String correlationKey,
            IncidentStatus status
    );
}
