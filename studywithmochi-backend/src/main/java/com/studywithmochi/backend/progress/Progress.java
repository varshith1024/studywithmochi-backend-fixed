package com.studywithmochi.backend.progress;

import jakarta.persistence.*;

@Entity
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "total_study_minutes", nullable = false)
    private Integer totalStudyMinutes = 0;

    @Column(name = "focus_energy", nullable = false)
    private Integer focusEnergy = 0;

    public Progress() {
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

    public Integer getTotalStudyMinutes() {
        return totalStudyMinutes;
    }

    public void setTotalStudyMinutes(Integer totalStudyMinutes) {
        this.totalStudyMinutes = totalStudyMinutes;
    }

    public Integer getFocusEnergy() {
        return focusEnergy;
    }

    public void setFocusEnergy(Integer focusEnergy) {
        this.focusEnergy = focusEnergy;
    }
}