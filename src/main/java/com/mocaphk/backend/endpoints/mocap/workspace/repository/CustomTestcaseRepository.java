package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.CustomTestcase;

import java.util.List;

@Repository
public interface CustomTestcaseRepository extends JpaRepository<CustomTestcase, Long> {
    List<CustomTestcase> findByQuestionId(Long questionId);

    List<CustomTestcase> findByUserIdAndQuestionId(String userId, Long questionId);
}
