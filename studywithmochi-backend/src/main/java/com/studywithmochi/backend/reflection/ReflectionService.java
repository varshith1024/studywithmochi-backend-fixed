package com.studywithmochi.backend.reflection;

import com.studywithmochi.backend.common.exception.ResourceNotFoundException;
import com.studywithmochi.backend.reflection.dto.ReflectionRequest;
import com.studywithmochi.backend.session.StudySession;
import com.studywithmochi.backend.session.StudySessionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReflectionService {

    private final LearningReflectionRepository reflectionRepository;
    private final StudySessionRepository studySessionRepository;

    public ReflectionService(
            LearningReflectionRepository reflectionRepository,
            StudySessionRepository studySessionRepository
    ) {
        this.reflectionRepository = reflectionRepository;
        this.studySessionRepository = studySessionRepository;
    }

    public LearningReflection createReflection(
            Long userId,
            ReflectionRequest request
    ) {

        StudySession session = studySessionRepository
                .findById(request.getSessionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Study session not found"
                        )
                );

        // Make sure the session belongs to the logged-in user
        if (!session.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Study session not found"
            );
        }

        // Reflection is allowed only after completion
        if (!"COMPLETED".equals(session.getStatus())) {
            throw new IllegalArgumentException(
                    "Reflection can only be added after completing the study session"
            );
        }

        // Only one reflection per session
        if (reflectionRepository
                .findBySessionId(request.getSessionId())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Reflection already exists for this session"
            );
        }

        LearningReflection reflection =
                new LearningReflection();

        reflection.setUserId(userId);
        reflection.setSessionId(request.getSessionId());
        reflection.setAnswer(request.getAnswer());
        reflection.setCreatedAt(LocalDateTime.now());

        return reflectionRepository.save(reflection);
    }

    public java.util.List<LearningReflection> getUserReflections(
        Long userId
) {
    return reflectionRepository.findByUserId(userId);
}
}