package com.studywithmochi.backend.subject;

import com.studywithmochi.backend.subject.dto.SubjectRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Subject> createSubject(
            @Valid @RequestBody SubjectRequest request,
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        Subject subject = subjectService.createSubject(
                userId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subject);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Subject>> getSubjects(
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        List<Subject> subjects =
                subjectService.getUserSubjects(userId);

        return ResponseEntity.ok(subjects);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request,
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        Subject updatedSubject =
                subjectService.updateSubject(
                        userId,
                        id,
                        request
                );

        return ResponseEntity.ok(updatedSubject);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        subjectService.deleteSubject(
                userId,
                id
        );

        return ResponseEntity.noContent().build();
    }
}