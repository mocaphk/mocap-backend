package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.AttemptResult;

@Repository
public interface AttemptResultRepository extends JpaRepository<AttemptResult, Long> {
}
