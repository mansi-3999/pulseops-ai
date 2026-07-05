package com.mansi.pulseops.incident.repository;

import com.mansi.pulseops.incident.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    List<Incident> findByStatusOrderByDetectedAtDesc(IncidentStatus status);

    List<Incident> findBySeverityOrderByDetectedAtDesc(Severity severity);

    Optional<Incident> findByCorrelationKeyAndStatusNot(
            String correlationKey,
            IncidentStatus status
    );
}
