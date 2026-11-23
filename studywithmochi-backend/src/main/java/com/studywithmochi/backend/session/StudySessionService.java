package com.studywithmochi.backend.session;

import com.studywithmochi.backend.common.exception.ResourceNotFoundException;
import com.studywithmochi.backend.progress.ProgressService;
import com.studywithmochi.backend.session.dto.StudySessionRequest;
import com.studywithmochi.backend.subject.Subject;
import com.studywithmochi.backend.subject.SubjectRepository;
import com.studywithmochi.backend.unlock.UnlockService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudySessionService {

    // A user may only have one of these at a time
    private static final List<String> ACTIVE_STATUSES =
            List.of("RUNNING", "PAUSED");

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final ProgressService progressService;
    private final UnlockService unlockService;

    public StudySessionService(
            StudySessionRepository studySessionRepository,
            SubjectRepository subjectRepository,
            ProgressService progressService,
            UnlockService unlockService
    ) {
        this.studySessionRepository = studySessionRepository;
        this.subjectRepository = subjectRepository;
        this.progressService = progressService;
        this.unlockService = unlockService;
    }

    // START SESSION
    @Transactional
    public StudySession startSession(
            Long userId,
            StudySessionRequest request
    ) {
        Subject subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subject not found")
                );

        if (!subject.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Subject not found");
        }

        if (request.getDurationMinutes() == null
                || request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Study duration must be greater than zero"
            );
        }

        // One active session per user
        if (studySessionRepository.existsByUserIdAndStatusIn(
                userId,
                ACTIVE_STATUSES
        )) {
            throw new IllegalArgumentException(
                    "You already have an active study session"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        StudySession session = new StudySession();

        session.setUserId(userId);
        session.setSubjectId(request.getSubjectId());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setActualStudySeconds(0);

        // Preserve the original start time for history.
        session.setStartedAt(now);

        // Track the beginning of the current active period.
        session.setActiveStartedAt(now);

        session.setPausedAt(null);
        session.setCompletedAt(null);
        session.setStatus("RUNNING");

        return studySessionRepository.save(session);
    }

    // PAUSE SESSION
    @Transactional
    public StudySession pauseSession(Long userId, Long sessionId) {
        StudySession session = getOwnedSession(userId, sessionId);

        if (!"RUNNING".equals(session.getStatus())) {
            throw new IllegalArgumentException(
                    "Only running sessions can be paused"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activeStartedAt = session.getActiveStartedAt();

        if (activeStartedAt == null) {
            throw new IllegalStateException(
                    "Active start time is missing"
            );
        }

        long elapsedSeconds = Duration
                .between(activeStartedAt, now)
                .getSeconds();

        int accumulatedSeconds = Math.toIntExact(
                (long) session.getActualStudySeconds()
                        + Math.max(0, elapsedSeconds)
        );

        session.setActualStudySeconds(accumulatedSeconds);

        // Do not modify startedAt.
        session.setActiveStartedAt(null);
        session.setPausedAt(now);
        session.setStatus("PAUSED");

        return studySessionRepository.save(session);
    }

    // RESUME SESSION
    @Transactional
    public StudySession resumeSession(Long userId, Long sessionId) {
        StudySession session = getOwnedSession(userId, sessionId);

        if (!"PAUSED".equals(session.getStatus())) {
            throw new IllegalArgumentException(
                    "Only paused sessions can be resumed"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // Preserve the original startedAt.
        session.setActiveStartedAt(now);
        session.setPausedAt(null);
        session.setStatus("RUNNING");

        return studySessionRepository.save(session);
    }

    // STOP SESSION: DISCARD ALL REWARDS
    @Transactional
    public StudySession stopSession(Long userId, Long sessionId) {
        StudySession session = getOwnedSession(userId, sessionId);

        if (!"RUNNING".equals(session.getStatus())
                && !"PAUSED".equals(session.getStatus())) {
            throw new IllegalArgumentException(
                    "Only running or paused sessions can be stopped"
            );
        }

        session.setStatus("STOPPED");
        session.setCompletedAt(LocalDateTime.now());

        // Discard all accumulated study time.
        session.setActualStudySeconds(0);

        session.setActiveStartedAt(null);
        session.setPausedAt(null);

        // No progress or Focus Energy is awarded.
        return studySessionRepository.save(session);
    }

    // COMPLETE SESSION: AWARD REWARDS ONLY AFTER FULL DURATION
    @Transactional
    public StudySession completeSession(Long userId, Long sessionId) {
        StudySession session = getOwnedSession(userId, sessionId);

        if (!"RUNNING".equals(session.getStatus())) {
            throw new IllegalArgumentException(
                    "Only running sessions can be completed"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activeStartedAt = session.getActiveStartedAt();

        if (activeStartedAt == null) {
            throw new IllegalStateException(
                    "Active start time is missing"
            );
        }

        long currentActiveSeconds = Duration
                .between(activeStartedAt, now)
                .getSeconds();

        long totalActiveSeconds =
                (long) session.getActualStudySeconds()
                        + Math.max(0, currentActiveSeconds);

        long requiredSeconds =
                (long) session.getDurationMinutes() * 60;

        // Enforce the full duration using server-side timestamps.
        if (totalActiveSeconds < requiredSeconds) {
            throw new IllegalArgumentException(
                    "The selected study duration has not finished"
            );
        }

        // Credit exactly the selected duration.
        session.setActualStudySeconds(
                Math.toIntExact(requiredSeconds)
        );

        session.setStatus("COMPLETED");
        session.setCompletedAt(now);
        session.setActiveStartedAt(null);
        session.setPausedAt(null);

        StudySession completedSession =
                studySessionRepository.save(session);

        // Award study minutes and Focus Energy only after completion.
        progressService.addStudyMinutes(
                userId,
                completedSession.getDurationMinutes()
        );

        // Evaluate unlock thresholds after awarding Focus Energy.
        unlockService.checkAndCreateUnlocks(userId);

        return completedSession;
    }

    // GET ACTIVE (RUNNING OR PAUSED) SESSION
    @Transactional(readOnly = true)
    public Optional<StudySession> getActiveSession(Long userId) {
        return studySessionRepository
                .findFirstByUserIdAndStatusInOrderByStartedAtDesc(
                        userId,
                        ACTIVE_STATUSES
                );
    }

    // GET USER SESSIONS
    @Transactional(readOnly = true)
    public List<StudySession> getUserSessions(Long userId) {
        return studySessionRepository
                .findByUserIdOrderByStartedAtDesc(userId);
    }

    // FIND SESSION AND VERIFY OWNERSHIP
    private StudySession getOwnedSession(Long userId, Long sessionId) {
        StudySession session = studySessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Study session not found"
                        )
                );

        if (!session.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Study session not found"
            );
        }

        return session;
    }
}