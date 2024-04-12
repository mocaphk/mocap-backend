package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByQuestionId(Long questionId);

    List<Attempt> findTop5ByUserIdAndQuestionIdAndIsSubmittedTrueOrderByUpdatedAtDesc(String userId, Long questionId);

    Attempt findFirstByUserIdAndQuestionIdAndIsSubmittedTrueOrderByUpdatedAtDesc(String userId, Long questionId);

    Attempt findFirstByUserIdAndQuestionIdOrderByUpdatedAtDesc(String userId, Long questionId);
}
