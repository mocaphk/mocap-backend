package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.CodeExecutionResult;

@Repository
public interface CodeExecutionResultRepository extends JpaRepository<CodeExecutionResult, Long> {
}
