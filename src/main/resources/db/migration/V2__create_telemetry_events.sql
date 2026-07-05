CREATE TABLE telemetry_events (
    id UUID PRIMARY KEY,

    service_name VARCHAR(100) NOT NULL,

    event_type VARCHAR(100) NOT NULL,

    severity VARCHAR(20) NOT NULL,

    message TEXT NOT NULL,

    trace_id VARCHAR(150),

    occurred_at TIMESTAMPTZ NOT NULL,

    received_at TIMESTAMPTZ NOT NULL,

    incident_id UUID,

    CONSTRAINT fk_telemetry_incident
        FOREIGN KEY (incident_id)
        REFERENCES incidents(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_telemetry_service_name
    ON telemetry_events(service_name);

CREATE INDEX idx_telemetry_event_type
    ON telemetry_events(event_type);

CREATE INDEX idx_telemetry_trace_id
    ON telemetry_events(trace_id);

CREATE INDEX idx_telemetry_occurred_at
    ON telemetry_events(occurred_at DESC);

CREATE INDEX idx_telemetry_incident_id
    ON telemetry_events(incident_id);