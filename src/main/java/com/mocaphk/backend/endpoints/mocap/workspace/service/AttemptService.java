package com.mocaphk.backend.endpoints.mocap.workspace.service;

import com.mocaphk.backend.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.mocaphk.backend.endpoints.mocap.user.dto.GetUserOutput;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.AttemptRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttemptService {
    private final AttemptRepository attemptRepository;

    public Attempt getAttemptById(Long id) {
        return attemptRepository.findById(id).orElse(null);
    }

    public List<Attempt> getAttemptsByQuestionId(String userId, Long questionId) {
        return attemptRepository.findTop5ByUserIdAndQuestionIdAndIsSubmittedTrueOrderByUpdatedAtDesc(userId, questionId)
                .stream()
                .sorted(Comparator.comparing(Attempt::getUpdatedAt))
                .collect(Collectors.toList());
    }


    public Attempt getLatestSubmissionByQuestionId(String userId, Long questionId) {
        return attemptRepository.findFirstByUserIdAndQuestionIdAndIsSubmittedTrueOrderByUpdatedAtDesc(userId, questionId);
    }

    public Attempt getLatestUpdateByQuestionId(String userId, Long questionId) {
        return attemptRepository.findFirstByUserIdAndQuestionIdOrderByUpdatedAtDesc(userId, questionId);
    }

    public Attempt createAttempt(String userId, CreateAttemptInput input) {
        Attempt attempt = new Attempt();
        attempt.setUserId(userId);
        attempt.setQuestionId(input.questionId());
        attempt.setCode(input.code());
        attempt.setCreatedAt(DateUtils.now());
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
        attemptRepository.save(attempt);
        return attempt;
    }

    public List<GetUserOutput> getSubmittedStudents(Long questionId){
        List<MocapUser> students = attemptRepository.findDistinctUsersBySubmittedAttempts(questionId);
        List<GetUserOutput> sortedStudents = students.stream()
                .sorted(Comparator.comparing(MocapUser::getUsername))
                .map(user -> new GetUserOutput(user.getId(), user.getUsername()))
                .toList();
        return sortedStudents;
    }
}
