package com.studywithmochi.backend.session;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    // User-selected target duration
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    // Accumulated active study time in seconds, excluding pauses
    @Column(name = "actual_study_seconds", nullable = false)
    private Integer actualStudySeconds = 0;

    // Original session start time; never changes
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    // Start time of the current active study period
    @Column(name = "active_started_at")
    private LocalDateTime activeStartedAt;

    // Session completion or stop time
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Timestamp when the current pause began
    @Column(name = "paused_at")
    private LocalDateTime pausedAt;

    @Column(nullable = false, length = 30)
    private String status;

    public StudySession() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getActualStudySeconds() {
        return actualStudySeconds;
    }

    public void setActualStudySeconds(Integer actualStudySeconds) {
        this.actualStudySeconds = actualStudySeconds;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getActiveStartedAt() {
        return activeStartedAt;
    }

    public void setActiveStartedAt(LocalDateTime activeStartedAt) {
        this.activeStartedAt = activeStartedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(LocalDateTime pausedAt) {
        this.pausedAt = pausedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}