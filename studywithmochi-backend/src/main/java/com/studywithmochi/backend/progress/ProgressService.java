package com.studywithmochi.backend.progress;

import com.studywithmochi.backend.progress.dto.DailyStudyStatsResponse;
import com.studywithmochi.backend.progress.dto.DashboardStatsResponse;
import com.studywithmochi.backend.progress.dto.SubjectStudyStatsResponse;
import com.studywithmochi.backend.session.StudySession;
import com.studywithmochi.backend.session.StudySessionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final StudySessionRepository studySessionRepository;

    public ProgressService(
            ProgressRepository progressRepository,
            StudySessionRepository studySessionRepository
    ) {
        this.progressRepository = progressRepository;
        this.studySessionRepository = studySessionRepository;
    }

    // GET OR CREATE USER PROGRESS
    @Transactional
    public Progress getOrCreateProgress(Long userId) {
        return progressRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Progress progress = new Progress();
                    progress.setUserId(userId);
                    progress.setTotalStudyMinutes(0);
                    progress.setFocusEnergy(0);

                    return progressRepository.save(progress);
                });
    }

    // AWARD STUDY MINUTES AND FOCUS ENERGY
    @Transactional
    public Progress addStudyMinutes(Long userId, Integer minutes) {
        if (minutes == null || minutes <= 0) {
            throw new IllegalArgumentException(
                    "Study minutes must be greater than zero"
            );
        }

        Progress progress = getOrCreateProgress(userId);

        progress.setTotalStudyMinutes(
                Math.addExact(progress.getTotalStudyMinutes(), minutes)
        );

        // One completed study minute = one Focus Energy.
        progress.setFocusEnergy(
                Math.addExact(progress.getFocusEnergy(), minutes)
        );

        return progressRepository.save(progress);
    }

    // GET USER PROGRESS
    @Transactional
    public Progress getProgress(Long userId) {
        return getOrCreateProgress(userId);
    }

    // GET TODAY AND THIS WEEK'S COMPLETED STUDY MINUTES
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Long userId) {
        LocalDate today = LocalDate.now();

        LocalDateTime todayStart = today.atStartOfDay();

        LocalDate weekStart = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();

        long todayStudyMinutes =
                studySessionRepository
                        .sumDurationMinutesByUserIdAndStatusAndStartedAtAfter(
                                userId,
                                "COMPLETED",
                                todayStart
                        );

        long weekStudyMinutes =
                studySessionRepository
                        .sumDurationMinutesByUserIdAndStatusAndStartedAtAfter(
                                userId,
                                "COMPLETED",
                                weekStartDateTime
                        );

        return new DashboardStatsResponse(
                todayStudyMinutes,
                weekStudyMinutes
        );
    }

    // GET SUBJECT-WISE COMPLETED STUDY MINUTES
    @Transactional(readOnly = true)
    public List<SubjectStudyStatsResponse> getSubjectStudyStats(Long userId) {
        List<Object[]> results =
                studySessionRepository.findSubjectStudyStats(
                        userId,
                        "COMPLETED"
                );

        return results.stream()
                .map(row -> new SubjectStudyStatsResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    // GET THIS WEEK'S DAILY COMPLETED STUDY MINUTES
    @Transactional(readOnly = true)
    public List<DailyStudyStatsResponse> getWeeklyStudyHistory(Long userId) {
        LocalDate today = LocalDate.now();

        LocalDate weekStart = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();

        List<StudySession> sessions =
                studySessionRepository
                        .findByUserIdAndStatusAndStartedAtAfterOrderByStartedAtAsc(
                                userId,
                                "COMPLETED",
                                weekStartDateTime
                        );

        Map<LocalDate, Long> minutesByDate = new HashMap<>();

        for (StudySession session : sessions) {
            LocalDate sessionDate = session.getStartedAt().toLocalDate();

            minutesByDate.merge(
                    sessionDate,
                    session.getDurationMinutes().longValue(),
                    Long::sum
            );
        }

        List<DailyStudyStatsResponse> history = new ArrayList<>();

        for (LocalDate date = weekStart;
             !date.isAfter(today);
             date = date.plusDays(1)) {

            history.add(
                    new DailyStudyStatsResponse(
                            date,
                            minutesByDate.getOrDefault(date, 0L)
                    )
            );
        }

        return history;
    }
}