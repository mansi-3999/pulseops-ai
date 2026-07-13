/**
 * Manages the lifecycle of incidents including creation,
 * retrieval, status updates, and correlation results.
 */

package com.mansi.pulseops.incident.service;

import com.mansi.pulseops.common.exception.IncidentNotFoundException;
import com.mansi.pulseops.incident.domain.Incident;
import com.mansi.pulseops.incident.domain.IncidentStatus;
import com.mansi.pulseops.incident.domain.Severity;
import com.mansi.pulseops.incident.dto.CreateIncidentRequest;
import com.mansi.pulseops.incident.dto.IncidentResponse;
import com.mansi.pulseops.incident.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class IncidentService {
    private final IncidentRepository repository;

    public IncidentService(IncidentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IncidentResponse create(CreateIncidentRequest r) {
        var now = OffsetDateTime.now();
        var i = new Incident(UUID.randomUUID(), r.title().trim(), r.description(), r.severity(), IncidentStatus.OPEN, r.detectedAt(), null, now, now);
        return toResponse(repository.save(i));
    }

    public IncidentResponse getById(UUID id) {
        return toResponse(find(id));
    }

    public List<IncidentResponse> getAll(IncidentStatus status, Severity severity) {
        List<Incident> list = status != null ? repository.findByStatusOrderByDetectedAtDesc(status) : severity != null ? repository.findBySeverityOrderByDetectedAtDesc(severity) : repository.findAll();
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional
    public IncidentResponse updateStatus(UUID id, IncidentStatus status) {
        var i = find(id);
        i.updateStatus(status);
        return toResponse(i);
    }

    private Incident find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
    }

    private IncidentResponse toResponse(Incident i) {
        return new IncidentResponse(i.getId(), i.getTitle(), i.getDescription(), i.getSeverity(), i.getStatus(), i.getDetectedAt(), i.getResolvedAt(), i.getCreatedAt(), i.getUpdatedAt(), i.getCorrelationKey(), i.getSource(), i.getEventCount(),
                i.getLastEventAt());
    }
}
