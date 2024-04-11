package com.mocaphk.backend.endpoints.mocap.course.repository;

import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT DISTINCT c.code FROM Course c")
    List<String> findAllCodes();

    List<Course> findAllByCode(String code);
}
