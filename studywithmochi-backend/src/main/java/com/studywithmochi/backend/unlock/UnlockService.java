package com.studywithmochi.backend.unlock;

import com.studywithmochi.backend.progress.Progress;
import com.studywithmochi.backend.progress.ProgressService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UnlockService {

    private final UnlockRepository unlockRepository;
    private final ProgressService progressService;

    public UnlockService(
            UnlockRepository unlockRepository,
            ProgressService progressService
    ) {
        this.unlockRepository = unlockRepository;
        this.progressService = progressService;
    }

    public void checkAndCreateUnlocks(Long userId) {

        Progress progress = progressService.getProgress(userId);

        int focusEnergy = progress.getFocusEnergy();

        checkUnlock(userId, "TINY_PLANT", 30, focusEnergy);
        checkUnlock(userId, "ROOM_DECORATION", 120, focusEnergy);
        checkUnlock(userId, "PET_FRIEND", 300, focusEnergy);
        checkUnlock(userId, "LIBRARY", 600, focusEnergy);
        checkUnlock(userId, "GARDEN", 1200, focusEnergy);
        checkUnlock(userId, "NEW_AREA", 3000, focusEnergy);
    }

    private void checkUnlock(
            Long userId,
            String unlockType,
            int requiredFocusEnergy,
            int focusEnergy
    ) {

        if (focusEnergy < requiredFocusEnergy) {
            return;
        }

        boolean alreadyUnlocked =
                unlockRepository
                        .findByUserIdAndUnlockType(
                                userId,
                                unlockType
                        )
                        .isPresent();

        if (alreadyUnlocked) {
            return;
        }

        Unlock unlock = new Unlock();

        unlock.setUserId(userId);
        unlock.setUnlockType(unlockType);
        unlock.setUnlockedAt(LocalDateTime.now());

        unlockRepository.save(unlock);
    }

    public void refreshUnlocks(Long userId) {
    checkAndCreateUnlocks(userId);
}
public java.util.List<Unlock> getUserUnlocks(Long userId) {
    return unlockRepository.findByUserId(userId);
}
}