package com.mansi.pulseops.incident.dto;
import com.mansi.pulseops.incident.domain.Severity;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
public record CreateIncidentRequest(@NotBlank @Size(max=255) String title,@Size(max=5000) String description,@NotNull Severity severity,@NotNull OffsetDateTime detectedAt) {}
