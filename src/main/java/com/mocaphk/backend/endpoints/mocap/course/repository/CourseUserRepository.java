package com.mocaphk.backend.endpoints.mocap.course.repository;

import com.mocaphk.backend.endpoints.mocap.course.model.CourseUser;
import com.mocaphk.backend.endpoints.mocap.course.model.CourseUserId;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.enums.CourseRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseUserRepository extends JpaRepository<CourseUser, CourseUserId> {
    List<CourseUser> findByUserId(String userId);

    List<CourseUser> findByCourseId(Long courseId);

    @Query("SELECT mu FROM MocapUser mu LEFT JOIN CourseUser cu ON mu.id = cu.user.id AND cu.course.id = :courseId WHERE cu.user.id IS NULL")
    List<MocapUser> findUsersNotInCourseByCourseId(Long courseId);

    @Query("SELECT cu FROM CourseUser cu JOIN cu.roles r WHERE r.role = :role AND cu.course.id = :courseId")
    List<CourseUser> findByCourseIdAndRole(@Param("courseId") Long courseId, @Param("role") CourseRole role);

    @Query("SELECT cu FROM CourseUser cu WHERE cu.user.username LIKE %:username% AND cu.course.id = :courseId")
    List<CourseUser> searchByCourseIdAndUsername(@Param("courseId") Long courseId, @Param("username") String username);
}
