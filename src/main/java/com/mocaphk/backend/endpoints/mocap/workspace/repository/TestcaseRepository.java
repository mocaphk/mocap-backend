package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.Testcase;

import java.util.List;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Long> {
    List<Testcase> findByQuestionId(Long questionId);

    @Query("select t from Testcase t where t.questionId = ?1 and t.isHidden = false")
    List<Testcase> findByQuestionIdAndIsHiddenFalse(Long questionId);
}
