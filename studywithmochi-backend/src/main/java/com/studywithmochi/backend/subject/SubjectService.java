package com.studywithmochi.backend.subject;

import com.studywithmochi.backend.common.exception.ResourceNotFoundException;
import com.studywithmochi.backend.subject.dto.SubjectRequest;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(
            SubjectRepository subjectRepository
    ) {
        this.subjectRepository = subjectRepository;
    }

    // CREATE
    public Subject createSubject(
            Long userId,
            SubjectRequest request
    ) {

        Subject subject = new Subject();

        subject.setName(request.getName());
        subject.setUserId(userId);
        subject.setCreatedAt(LocalDateTime.now());

        return subjectRepository.save(subject);
    }

    // GET ALL
    public List<Subject> getUserSubjects(Long userId) {

        return subjectRepository.findByUserId(userId);
    }

    // UPDATE
    public Subject updateSubject(
            Long userId,
            Long subjectId,
            SubjectRequest request
    ) {

        Subject subject = subjectRepository
                .findById(subjectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found"
                        )
                );

        if (!subject.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Subject not found"
            );
        }

        subject.setName(request.getName());

        return subjectRepository.save(subject);
    }

    // DELETE
    public void deleteSubject(
            Long userId,
            Long subjectId
    ) {

        Subject subject = subjectRepository
                .findById(subjectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found"
                        )
                );

        if (!subject.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Subject not found"
            );
        }

        subjectRepository.delete(subject);
    }
}