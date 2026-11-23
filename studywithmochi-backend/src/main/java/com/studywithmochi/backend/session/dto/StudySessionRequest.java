package com.studywithmochi.backend.session.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class StudySessionRequest {

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Study duration is required")
    @Positive(message = "Study duration must be greater than zero")
    @Max(value = 1440, message = "Study duration cannot exceed 1440 minutes")
    private Integer durationMinutes;

    public StudySessionRequest() {
    }

    public StudySessionRequest(Long subjectId, Integer durationMinutes) {
        this.subjectId = subjectId;
        this.durationMinutes = durationMinutes;
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
}