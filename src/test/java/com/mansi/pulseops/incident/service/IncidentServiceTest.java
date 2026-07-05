package com.mansi.pulseops.incident.service;

import com.mansi.pulseops.incident.domain.Incident;
import com.mansi.pulseops.incident.domain.IncidentStatus;
import com.mansi.pulseops.incident.domain.Severity;
import com.mansi.pulseops.incident.dto.CreateIncidentRequest;
import com.mansi.pulseops.incident.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidentServiceTest {

    private final IncidentRepository repository =
            mock(IncidentRepository.class);

    private final IncidentService service =
            new IncidentService(repository);

    @Test
    void shouldCreateOpenIncident() {

        // Arrange
        when(repository.save(any(Incident.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        var request = new CreateIncidentRequest(
                "Payment failures",
                "Timeout spike",
                Severity.HIGH,
                OffsetDateTime.now()
        );

        // Act
        var response = service.create(request);

        // Assert
        var captor =
                ArgumentCaptor.forClass(Incident.class);

        verify(repository).save(captor.capture());

        Incident savedIncident = captor.getValue();

        assertThat(savedIncident.getStatus())
                .isEqualTo(IncidentStatus.OPEN);

        assertThat(savedIncident.getId())
                .isNotNull();

        assertThat(response.title())
                .isEqualTo("Payment failures");

        assertThat(response.status())
                .isEqualTo(IncidentStatus.OPEN);
    }
}