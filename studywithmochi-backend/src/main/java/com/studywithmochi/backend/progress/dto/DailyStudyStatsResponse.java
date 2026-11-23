package com.studywithmochi.backend.progress.dto;

import java.time.LocalDate;

public class DailyStudyStatsResponse {

    private LocalDate date;
    private long studyMinutes;

    public DailyStudyStatsResponse() {
    }

    public DailyStudyStatsResponse(LocalDate date, long studyMinutes) {
        this.date = date;
        this.studyMinutes = studyMinutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getStudyMinutes() {
        return studyMinutes;
    }

    public void setStudyMinutes(long studyMinutes) {
        this.studyMinutes = studyMinutes;
    }
}