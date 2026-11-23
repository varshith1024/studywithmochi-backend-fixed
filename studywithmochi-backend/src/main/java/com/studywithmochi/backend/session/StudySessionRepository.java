package com.studywithmochi.backend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudySessionRepository
        extends JpaRepository<StudySession, Long> {

    List<StudySession> findByUserId(Long userId);

    boolean existsByUserIdAndStatusIn(Long userId, Collection<String> statuses);

    Optional<StudySession> findFirstByUserIdAndStatusInOrderByStartedAtDesc(
            Long userId,
            Collection<String> statuses
    );

    List<StudySession> findByUserIdOrderByStartedAtDesc(Long userId);

    @Query("""
            SELECT COALESCE(SUM(s.durationMinutes), 0)
            FROM StudySession s
            WHERE s.userId = :userId
            AND s.status = :status
            AND s.startedAt >= :startTime
            """)
    long sumDurationMinutesByUserIdAndStatusAndStartedAtAfter(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime
    );

    @Query("""
            SELECT s.subjectId, sub.name, SUM(s.durationMinutes)
            FROM StudySession s
            JOIN Subject sub ON sub.id = s.subjectId
            WHERE s.userId = :userId
            AND s.status = :status
            GROUP BY s.subjectId, sub.name
            ORDER BY SUM(s.durationMinutes) DESC
            """)
    List<Object[]> findSubjectStudyStats(
            @Param("userId") Long userId,
            @Param("status") String status
    );

    List<StudySession> findByUserIdAndStatusAndStartedAtAfterOrderByStartedAtAsc(
            Long userId,
            String status,
            LocalDateTime startTime
    );
}