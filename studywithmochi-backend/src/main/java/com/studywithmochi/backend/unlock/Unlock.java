package com.studywithmochi.backend.unlock;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "unlocks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_user_unlock",
            columnNames = {"user_id", "unlock_type"}
        )
    }
)
public class Unlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "unlock_type", nullable = false, length = 50)
    private String unlockType;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    public Unlock() {
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

    public String getUnlockType() {
        return unlockType;
    }

    public void setUnlockType(String unlockType) {
        this.unlockType = unlockType;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}