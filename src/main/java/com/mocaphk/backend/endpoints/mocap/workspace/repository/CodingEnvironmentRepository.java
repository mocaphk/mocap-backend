package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.CodingEnvironment;

import java.util.List;

@Repository
public interface CodingEnvironmentRepository extends JpaRepository<CodingEnvironment, Long> {

    List<CodingEnvironment> findByCourse_Assignments_IdOrCourseIdNull(Long id);
}
