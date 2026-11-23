-- Clean up first: if a user already has several RUNNING/PAUSED sessions,
-- keep only their newest one and stop (discard) the older ones.
UPDATE study_sessions
SET status = 'STOPPED',
    completed_at = CURRENT_TIMESTAMP,
    actual_study_seconds = 0,
    active_started_at = NULL,
    paused_at = NULL
WHERE status IN ('RUNNING', 'PAUSED')
  AND id NOT IN (
      SELECT MAX(id)
      FROM study_sessions
      WHERE status IN ('RUNNING', 'PAUSED')
      GROUP BY user_id
  );

-- Database-level guarantee: at most one active session per user
-- (also protects against two start requests arriving at the same moment)
CREATE UNIQUE INDEX uq_one_active_session_per_user
    ON study_sessions (user_id)
    WHERE status IN ('RUNNING', 'PAUSED');
