package com.mocaphk.backend.endpoints.mocap.course.repository;

import com.mocaphk.backend.endpoints.mocap.course.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseIdAndDateDueBetween(Long courseId, String start, String end);
}
