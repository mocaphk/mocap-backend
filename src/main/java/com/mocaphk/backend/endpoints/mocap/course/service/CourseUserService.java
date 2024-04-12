package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.endpoints.mocap.course.dto.GetCourseUserOutput;
import com.mocaphk.backend.endpoints.mocap.course.model.*;
import com.mocaphk.backend.endpoints.mocap.course.repository.AssignmentRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.dto.GetUserOutput;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import com.mocaphk.backend.enums.CourseRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseUserService {
    private final CourseUserRepository courseUserRepository;
    private final MocapUserRepository mocapUserRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;

    public List<CourseRole> getCourseUserRoles(Long courseId, String userId) {
        CourseUserId id = new CourseUserId(courseId, userId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null) {
            return null;
        }

        return courseUser.getRoles().stream().map(CourseUserRole::getRole).toList();
    }

    public List<CourseRole> getAssignmentUserRoles(Long assignmentId, String userId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);

        if (assignment == null) {
            return null;
        }

        return getCourseUserRoles(assignment.getCourse().getId(), userId);
    }

    public List<Course> getCoursesByUserId(String userId) {
        List<CourseUser> courseUsers = courseUserRepository.findByUserId(userId);
        return courseUsers.stream().map(CourseUser::getCourse).toList();
    }

    public List<MocapUser> getUsersByCourseIdAndRole(Long courseId, CourseRole role) {
        return courseUserRepository.findByCourseIdAndRole(courseId, role).stream().map(CourseUser::getUser).toList();
    }

    public List<GetCourseUserOutput> getUsersByCourseId(Long courseId, String requesterId) {
        // Check if the requested user is admin or not
        CourseUserId id = new CourseUserId(courseId, requesterId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null || !courseUser.getRoles().stream().map(CourseUserRole::getRole).toList().contains(CourseRole.ADMIN)) {
            return null;
        }

        List<CourseUser> courseUsers = courseUserRepository.findByCourseId(courseId);
        return courseUsers.stream().map(user -> new GetCourseUserOutput(user.getUser().getId(),
                user.getUser().getUsername(),
                user.getRoles().stream().map(CourseUserRole::getRole).toList())).toList();
    }

    public List<GetUserOutput> getUsersNotInCourseByCourseId(Long courseId, String requesterId) {
        // Check if the requested user is admin or not
        CourseUserId id = new CourseUserId(courseId, requesterId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null || !courseUser.getRoles().stream().map(CourseUserRole::getRole).toList().contains(CourseRole.ADMIN)) {
            return null;
        }

        List<MocapUser> users = courseUserRepository.findUsersNotInCourseByCourseId(courseId);
        log.debug("{}", users);
        return users.stream().map(user -> new GetUserOutput(user.getId(), user.getUsername())).toList();
    }

    public List<GetCourseUserOutput> searchByCourseIdAndUsername(Long courseId, String username, String requesterId) {
        // Check if the requested user is admin or not
        CourseUserId id = new CourseUserId(courseId, requesterId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null || !courseUser.getRoles().stream().map(CourseUserRole::getRole).toList().contains(CourseRole.ADMIN)) {
            return null;
        }

        List<CourseUser> courseUsers = courseUserRepository.searchByCourseIdAndUsername(courseId, username);
        return courseUsers.stream().map(user -> new GetCourseUserOutput(user.getUser().getId(),
                user.getUser().getUsername(),
                user.getRoles().stream().map(CourseUserRole::getRole).toList())).toList();
    }

    @Transactional
    public GetCourseUserOutput createCourseUser(Long courseId, String userId, String newUsername, List<CourseRole> roles) {
        // Check if the requested user is admin or not
        CourseUserId id = new CourseUserId(courseId, userId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null || !courseUser.getRoles().stream().map(CourseUserRole::getRole).toList().contains(CourseRole.ADMIN)) {
            return null;
        }

        // Update course user table
        MocapUser newUser = mocapUserRepository.findOneByUsername(newUsername);

        if (newUser == null) {
            return null;
        }

        Course course = courseRepository.findById(courseId).orElse(null);

        if (course == null) {
            return null;
        }

        log.debug("course: {}, user: {}", courseId, newUser.getId());

        CourseUser newCourseUser = new CourseUser();
        CourseUserId courseUserId = new CourseUserId(courseId, newUser.getId());
        newCourseUser.setId(courseUserId);
        newCourseUser.setCourse(course);
        newCourseUser.setUser(newUser);
        newCourseUser.setRoles(roles.stream().map(role -> new CourseUserRole(newCourseUser, role)).toList());

        CourseUser result = courseUserRepository.save(newCourseUser);

        return new GetCourseUserOutput(result.getUser().getId(), result.getUser().getUsername(), roles);
    }

    public Boolean deleteCourseUser(Long courseId, String userId, String requesterId) {
        // Check if the requested user is admin or not
        CourseUserId id = new CourseUserId(courseId, requesterId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null || !courseUser.getRoles().stream().map(CourseUserRole::getRole).toList().contains(CourseRole.ADMIN)) {
            return false;
        }

        CourseUserId courseUserId = new CourseUserId(courseId, userId);
        courseUserRepository.deleteById(courseUserId);
        return true;
    }
}
