CREATE TABLE ai_analyses (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    investigation_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    model_id VARCHAR(255),
    prompt_version VARCHAR(50) NOT NULL,
    analysis_text TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_ai_analysis_incident
        FOREIGN KEY (incident_id)
        REFERENCES incidents(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ai_analysis_investigation
        FOREIGN KEY (investigation_id)
        REFERENCES investigations(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_ai_analyses_incident_id
    ON ai_analyses(incident_id);

CREATE INDEX idx_ai_analyses_investigation_id
    ON ai_analyses(investigation_id);