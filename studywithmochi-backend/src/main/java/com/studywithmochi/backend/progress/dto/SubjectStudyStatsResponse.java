package com.studywithmochi.backend.progress.dto;

public class SubjectStudyStatsResponse {

    private Long subjectId;
    private String subjectName;
    private long studyMinutes;

    public SubjectStudyStatsResponse() {
    }

    public SubjectStudyStatsResponse(
            Long subjectId,
            String subjectName,
            long studyMinutes
    ) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.studyMinutes = studyMinutes;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getStudyMinutes() {
        return studyMinutes;
    }

    public void setStudyMinutes(long studyMinutes) {
        this.studyMinutes = studyMinutes;
    }
}