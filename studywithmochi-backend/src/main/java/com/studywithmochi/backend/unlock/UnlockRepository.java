package com.studywithmochi.backend.unlock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnlockRepository extends JpaRepository<Unlock, Long> {

    List<Unlock> findByUserId(Long userId);

    Optional<Unlock> findByUserIdAndUnlockType(
            Long userId,
            String unlockType
    );
}