ALTER TABLE incidents
    ADD COLUMN correlation_key VARCHAR(255);

ALTER TABLE incidents
    ADD COLUMN source VARCHAR(50) NOT NULL DEFAULT 'MANUAL';

ALTER TABLE incidents
    ADD COLUMN event_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE incidents
    ADD COLUMN last_event_at TIMESTAMPTZ;

CREATE INDEX idx_incidents_correlation_key
    ON incidents(correlation_key);

CREATE INDEX idx_incidents_source
    ON incidents(source);

CREATE INDEX idx_incidents_last_event_at
    ON incidents(last_event_at);

CREATE INDEX idx_telemetry_trace_occurred
    ON telemetry_events(trace_id, occurred_at);