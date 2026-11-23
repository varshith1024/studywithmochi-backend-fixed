package com.studywithmochi.backend.reflection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningReflectionRepository
        extends JpaRepository<LearningReflection, Long> {

    Optional<LearningReflection> findBySessionId(Long sessionId);

    List<LearningReflection> findByUserId(Long userId);
}