package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import com.mocaphk.backend.endpoints.keycloak.user.repository.UserRepository;
import com.mocaphk.backend.endpoints.mocap.course.dto.CreateCourseInput;
import com.mocaphk.backend.endpoints.mocap.course.model.*;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import com.mocaphk.backend.enums.CourseRole;
import com.mocaphk.backend.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseUserRepository courseUserRepository;
    private final MocapUserRepository mocapUserRepository;

    public Course getCourseById(Long id, String userId) {
        CourseUserId courseUserId = new CourseUserId(id, userId);
        CourseUser courseUser = courseUserRepository.findById(courseUserId).orElse(null);

        // Check if the user belongs to the course
        // TODO: Return error message with unauthorised access
        if (courseUser == null) {
            return null;
        }

        return courseRepository.findById(id).orElse(null);
    }

    public List<String> getCodes() {
        return courseRepository.findAllCodes();
    }

    public List<Course> getCoursesByCode(String courseCode) {
        return courseRepository.findAllByCode(courseCode);
    }

    public Course createCourse(CreateCourseInput input, String userId) {
        MocapUser user = mocapUserRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        // Update course table
        Course course = new Course();
        course.setCode(input.code());
        course.setName(input.name());
        course.setDescription(input.description());
        course.setBarColor(input.barColor());
        course.setYear(DateUtils.currentYear());
        String now = DateUtils.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        course.setAssignments(new ArrayList<Assignment>());
        course.setAnnouncements(new ArrayList<Announcement>());

        courseRepository.save(course);

        // Update course user table
        CourseUser courseUser = new CourseUser();
        CourseUserId courseUserId = new CourseUserId(course.getId(), user.getId());
        courseUser.setId(courseUserId);
        courseUser.setCourse(course);
        courseUser.setUser(user);
        courseUser.setRoles(new ArrayList<CourseUserRole>());

        CourseUserRole adminRole = new CourseUserRole();
        adminRole.setRole(CourseRole.ADMIN);
        adminRole.setCourseUser(courseUser);

        CourseUserRole lecturerRole = new CourseUserRole();
        lecturerRole.setRole(CourseRole.LECTURER);
        lecturerRole.setCourseUser(courseUser);

        courseUser.getRoles().add(adminRole);
        courseUser.getRoles().add(lecturerRole);

        courseUserRepository.save(courseUser);

        return course;
    }
}
