package com.studywithmochi.backend.session.dto;

import com.studywithmochi.backend.session.StudySession;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * What the frontend receives for a study session.
 *
 * remainingSeconds is computed on the server, so the timer never depends on
 * the browser clock or on timezone differences between server and browser.
 * The frontend simply counts down from this value while status == RUNNING.
 */
public record StudySessionResponse(
        Long id,
        Long subjectId,
        Integer durationMinutes,
        Integer actualStudySeconds,
        long remainingSeconds,
        String status,
        LocalDateTime startedAt,
        LocalDateTime activeStartedAt,
        LocalDateTime pausedAt,
        LocalDateTime completedAt
) {

    public static StudySessionResponse from(StudySession session) {

        long requiredSeconds = (long) session.getDurationMinutes() * 60;
        long studiedSeconds = session.getActualStudySeconds();

        boolean running = "RUNNING".equals(session.getStatus());
        boolean paused = "PAUSED".equals(session.getStatus());

        // A running session is still accumulating time in the current active period
        if (running && session.getActiveStartedAt() != null) {
            long currentPeriod = Duration
                    .between(session.getActiveStartedAt(), LocalDateTime.now())
                    .getSeconds();

            studiedSeconds += Math.max(0, currentPeriod);
        }

        long remaining = (running || paused)
                ? Math.max(0, requiredSeconds - studiedSeconds)
                : 0;

        return new StudySessionResponse(
                session.getId(),
                session.getSubjectId(),
                session.getDurationMinutes(),
                session.getActualStudySeconds(),
                remaining,
                session.getStatus(),
                session.getStartedAt(),
                session.getActiveStartedAt(),
                session.getPausedAt(),
                session.getCompletedAt()
        );
    }
}
