CREATE TABLE incidents (
 id UUID PRIMARY KEY,
 title VARCHAR(255) NOT NULL,
 description TEXT,
 severity VARCHAR(20) NOT NULL,
 status VARCHAR(30) NOT NULL,
 detected_at TIMESTAMPTZ NOT NULL,
 resolved_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_severity ON incidents(severity);
CREATE INDEX idx_incidents_detected_at ON incidents(detected_at DESC);
