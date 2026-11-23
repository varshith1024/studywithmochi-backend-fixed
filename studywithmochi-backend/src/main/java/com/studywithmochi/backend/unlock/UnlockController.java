package com.studywithmochi.backend.unlock;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unlocks")
public class UnlockController {

    private final UnlockService unlockService;

    public UnlockController(UnlockService unlockService) {
        this.unlockService = unlockService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshUnlocks(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        unlockService.refreshUnlocks(userId);

        return ResponseEntity.ok().build();
    }
    
    @GetMapping
public ResponseEntity<java.util.List<Unlock>> getUnlocks(
        Authentication authentication
) {
    Long userId = (Long) authentication.getPrincipal();

    java.util.List<Unlock> unlocks =
            unlockService.getUserUnlocks(userId);

    return ResponseEntity.ok(unlocks);
}
}