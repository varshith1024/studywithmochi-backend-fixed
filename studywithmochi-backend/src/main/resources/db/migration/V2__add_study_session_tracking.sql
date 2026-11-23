ALTER TABLE study_sessions
ADD COLUMN actual_study_seconds INTEGER NOT NULL DEFAULT 0;

ALTER TABLE study_sessions
ADD COLUMN paused_at TIMESTAMP;

ALTER TABLE study_sessions
ADD COLUMN active_started_at TIMESTAMP;