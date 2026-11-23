package com.studywithmochi.backend.session;

import com.studywithmochi.backend.session.dto.StudySessionRequest;
import com.studywithmochi.backend.session.dto.StudySessionResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class StudySessionController {

    private final StudySessionService studySessionService;

    public StudySessionController(
            StudySessionService studySessionService
    ) {
        this.studySessionService = studySessionService;
    }

    // START SESSION
    @PostMapping
    public ResponseEntity<StudySessionResponse> startSession(
            @Valid @RequestBody StudySessionRequest request,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        StudySession session =
                studySessionService.startSession(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StudySessionResponse.from(session));
    }

    // PAUSE SESSION
    @PostMapping("/{id}/pause")
    public ResponseEntity<StudySessionResponse> pauseSession(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        StudySession session =
                studySessionService.pauseSession(userId, id);

        return ResponseEntity.ok(StudySessionResponse.from(session));
    }

    // RESUME SESSION
    @PostMapping("/{id}/resume")
    public ResponseEntity<StudySessionResponse> resumeSession(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        StudySession session =
                studySessionService.resumeSession(userId, id);

        return ResponseEntity.ok(StudySessionResponse.from(session));
    }

    // STOP SESSION
    @PostMapping("/{id}/stop")
    public ResponseEntity<StudySessionResponse> stopSession(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        StudySession session =
                studySessionService.stopSession(userId, id);

        return ResponseEntity.ok(StudySessionResponse.from(session));
    }

    // COMPLETE SESSION
    @PostMapping("/{id}/complete")
    public ResponseEntity<StudySessionResponse> completeSession(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        StudySession session =
                studySessionService.completeSession(userId, id);

        return ResponseEntity.ok(StudySessionResponse.from(session));
    }

    // GET ACTIVE (RUNNING or PAUSED) SESSION: 200 with the session, 204 if none
    @GetMapping("/active")
    public ResponseEntity<StudySessionResponse> getActiveSession(
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        return studySessionService.getActiveSession(userId)
                .map(session -> ResponseEntity.ok(
                        StudySessionResponse.from(session)
                ))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // GET USER SESSIONS
    @GetMapping
    public ResponseEntity<List<StudySessionResponse>> getUserSessions(
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);

        List<StudySessionResponse> sessions = studySessionService
                .getUserSessions(userId)
                .stream()
                .map(StudySessionResponse::from)
                .toList();

        return ResponseEntity.ok(sessions);
    }

    // GET AUTHENTICATED USER ID
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
