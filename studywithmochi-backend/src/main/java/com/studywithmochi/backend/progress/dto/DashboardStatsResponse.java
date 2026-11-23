package com.studywithmochi.backend.progress.dto;

public class DashboardStatsResponse {

    private long todayStudyMinutes;
    private long weekStudyMinutes;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(
            long todayStudyMinutes,
            long weekStudyMinutes
    ) {
        this.todayStudyMinutes = todayStudyMinutes;
        this.weekStudyMinutes = weekStudyMinutes;
    }

    public long getTodayStudyMinutes() {
        return todayStudyMinutes;
    }

    public void setTodayStudyMinutes(long todayStudyMinutes) {
        this.todayStudyMinutes = todayStudyMinutes;
    }

    public long getWeekStudyMinutes() {
        return weekStudyMinutes;
    }

    public void setWeekStudyMinutes(long weekStudyMinutes) {
        this.weekStudyMinutes = weekStudyMinutes;
    }
}