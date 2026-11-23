package com.studywithmochi.backend.progress;

import com.studywithmochi.backend.progress.dto.DailyStudyStatsResponse;
import com.studywithmochi.backend.progress.dto.DashboardStatsResponse;
import com.studywithmochi.backend.progress.dto.SubjectStudyStatsResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // GET USER PROGRESS
    @GetMapping
    public ResponseEntity<Progress> getProgress(
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        Progress progress = progressService.getProgress(userId);

        return ResponseEntity.ok(progress);
    }

    // GET DASHBOARD STATISTICS
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        DashboardStatsResponse stats =
                progressService.getDashboardStats(userId);

        return ResponseEntity.ok(stats);
    }

    // GET SUBJECT-WISE STUDY STATISTICS
    @GetMapping("/dashboard/subjects")
    public ResponseEntity<List<SubjectStudyStatsResponse>>
    getSubjectStudyStats(Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<SubjectStudyStatsResponse> stats =
                progressService.getSubjectStudyStats(userId);

        return ResponseEntity.ok(stats);
    }

    // GET WEEKLY STUDY HISTORY
    @GetMapping("/dashboard/history")
    public ResponseEntity<List<DailyStudyStatsResponse>>
    getWeeklyStudyHistory(Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<DailyStudyStatsResponse> history =
                progressService.getWeeklyStudyHistory(userId);

        return ResponseEntity.ok(history);
    }

    // EXTRACT AUTHENTICATED USER ID CONSISTENTLY
    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Long)) {

            throw new IllegalStateException(
                    "Authenticated user ID is unavailable"
            );
        }

        return (Long) authentication.getPrincipal();
    }
}