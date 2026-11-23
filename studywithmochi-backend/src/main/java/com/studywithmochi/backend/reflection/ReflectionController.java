package com.studywithmochi.backend.reflection;

import com.studywithmochi.backend.reflection.dto.ReflectionRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reflections")
public class ReflectionController {

    private final ReflectionService reflectionService;

    public ReflectionController(
            ReflectionService reflectionService
    ) {
        this.reflectionService = reflectionService;
    }

    @PostMapping
    public ResponseEntity<LearningReflection> createReflection(
            @Valid @RequestBody ReflectionRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        LearningReflection reflection =
                reflectionService.createReflection(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reflection);
    }

    @GetMapping
public ResponseEntity<java.util.List<LearningReflection>> getUserReflections(
        Authentication authentication
) {
    Long userId = (Long) authentication.getPrincipal();

    java.util.List<LearningReflection> reflections =
            reflectionService.getUserReflections(userId);

    return ResponseEntity.ok(reflections);
}
}