package com.mansi.pulseops.incident.dto;

import com.mansi.pulseops.incident.domain.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(@NotNull IncidentStatus status) {
}
