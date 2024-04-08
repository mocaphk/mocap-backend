package com.mocaphk.backend.endpoints.mocap.course.controller;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateCourseInput;
import com.mocaphk.backend.endpoints.mocap.course.model.Announcement;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import com.mocaphk.backend.endpoints.mocap.course.service.CourseService;
import com.mocaphk.backend.endpoints.mocap.course.service.CourseUserService;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.enums.CourseRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseUserService courseUserService;

    @QueryMapping(name = "course")
    public Course getCourse(Authentication authentication, @Argument Long id) {
        log.debug("getCourse: {}", id);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseService.getCourseById(id, authentication.getName());
    }

    @QueryMapping(name = "courses")
    public List<Course> getCoursesByUserId(Authentication authentication) {
        log.debug("getCourses");
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseUserService.getCoursesByUserId(authentication.getName());
    }

    @MutationMapping(name = "createCourse")
    public Course createCourse(Authentication authentication, @Argument CreateCourseInput courseInput) {
        log.debug("createCourse: {}", courseInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseService.createCourse(courseInput, authentication.getName());
    }

    @SchemaMapping
    public List<MocapUser> admins(Course course) {
        log.debug("admins: {}", course);
        return courseUserService.getUsersByCourseIdAndRole(course.getId(), CourseRole.ADMIN);
    }

    @SchemaMapping
    public List<MocapUser> lecturers(Course course) {
        log.debug("lecturers: {}", course);
        return courseUserService.getUsersByCourseIdAndRole(course.getId(), CourseRole.LECTURER);
    }

    @SchemaMapping
    public List<MocapUser> tutors(Course course) {
        log.debug("tutors: {}", course);
        return courseUserService.getUsersByCourseIdAndRole(course.getId(), CourseRole.TUTOR);
    }

    @SchemaMapping
    public List<MocapUser> students(Course course) {
        log.debug("students: {}", course);
        return courseUserService.getUsersByCourseIdAndRole(course.getId(), CourseRole.STUDENT);
    }
}
