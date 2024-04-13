package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByQuestionId(Long questionId);

    List<Attempt> findTop5ByUserIdAndQuestionIdAndIsSubmittedTrueOrderByUpdatedAtDesc(String userId, Long questionId);

    Attempt findFirstByUserIdAndQuestionIdAndIsSubmittedTrueOrderByUpdatedAtDesc(String userId, Long questionId);

    Attempt findFirstByUserIdAndQuestionIdOrderByUpdatedAtDesc(String userId, Long questionId);

    @Query("SELECT DISTINCT a.user FROM Attempt a WHERE a.isSubmitted = true AND a.question.id = :questionId ORDER BY a.user.username ASC")
    List<MocapUser> findDistinctUsersBySubmittedAttempts(@Param("questionId") Long questionId);
}
