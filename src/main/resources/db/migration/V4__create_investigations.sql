CREATE TABLE investigations (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    summary TEXT,
    probable_root_cause_service VARCHAR(255),
    confidence_score DOUBLE PRECISION,
    total_events INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_investigation_incident
        FOREIGN KEY (incident_id)
        REFERENCES incidents(id)
        ON DELETE CASCADE
);

CREATE TABLE investigation_services (
    investigation_id UUID NOT NULL,
    service_name VARCHAR(255) NOT NULL,

    CONSTRAINT fk_investigation_services
        FOREIGN KEY (investigation_id)
        REFERENCES investigations(id)
        ON DELETE CASCADE
);

CREATE TABLE investigation_evidence (
    id UUID PRIMARY KEY,
    investigation_id UUID NOT NULL,
    event_id UUID,
    service_name VARCHAR(255),
    evidence_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    score DOUBLE PRECISION NOT NULL DEFAULT 0,
    observed_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_investigation_evidence
        FOREIGN KEY (investigation_id)
        REFERENCES investigations(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_investigations_incident_id
    ON investigations(incident_id);

CREATE INDEX idx_investigation_evidence_investigation_id
    ON investigation_evidence(investigation_id);