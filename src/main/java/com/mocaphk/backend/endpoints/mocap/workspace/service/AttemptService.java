package com.mocaphk.backend.endpoints.mocap.workspace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.AttemptRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Attempt getAttemptById(Long id) {
        return attemptRepository.findById(id).orElse(null);
    }

    public List<Attempt> getAttemptsByQuestionId(String userId, Long questionId) {
        return attemptRepository.findByUserIdAndQuestionId(userId, questionId);
    }

    public Attempt getLatestAttemptByQuestionId(String userId, Long questionId) {
        return attemptRepository.findByUserIdAndQuestionId(userId, questionId).stream()
                .max((a, b) -> LocalDateTime.parse(a.getUpdatedAt(), dateTimeFormatter)
                        .compareTo(LocalDateTime.parse(b.getUpdatedAt(), dateTimeFormatter)))
                .orElse(null);
    }

    public Attempt createAttempt(String userId, CreateAttemptInput input) {
        Attempt attempt = new Attempt();
        attempt.setUserId(userId);
        attempt.setQuestionId(input.questionId());
        attempt.setCode(input.code());
        attempt.setCreatedAt(dateTimeFormatter.format(LocalDateTime.now()));
        attempt.setUpdatedAt(dateTimeFormatter.format(LocalDateTime.now()));
        attempt.setIsSubmitted(false);
        attemptRepository.save(attempt);
        return attempt;
    }

    public Attempt updateAttempt(Long id, UpdateAttemptInput input) {
        Attempt attempt = getAttemptById(id);
        if (attempt == null) {
            return null;
        }

        attempt.setCode(input.code());
        attempt.setUpdatedAt(dateTimeFormatter.format(LocalDateTime.now()));
        attemptRepository.save(attempt);
        return attempt;
    }
}
