package com.mocaphk.backend.endpoints.mocap.course.controller;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateCourseInput;
import com.mocaphk.backend.endpoints.mocap.course.dto.SearchCourseUsersByUsernameInput;
import com.mocaphk.backend.endpoints.mocap.course.model.Announcement;
import com.mocaphk.backend.endpoints.mocap.course.dto.CreateCourseUserInput;
import com.mocaphk.backend.endpoints.mocap.course.dto.GetCourseUserOutput;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import com.mocaphk.backend.endpoints.mocap.course.model.CourseUser;
import com.mocaphk.backend.endpoints.mocap.course.service.CourseService;
import com.mocaphk.backend.endpoints.mocap.course.service.CourseUserService;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.enums.CourseRole;
import com.mocaphk.backend.endpoints.mocap.user.dto.GetUserOutput;
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

    @QueryMapping(name = "getCourseUserRoles")
    public List<CourseRole> getCourseUserRoles(Authentication authentication, @Argument Long courseId) {
        log.debug("getCourseUserRoles {}", courseId);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseUserService.getCourseUserRoles(courseId, authentication.getName());
    }

    @QueryMapping(name = "courseUsers")
    public List<GetCourseUserOutput> getUsersByCourseId(Authentication authentication, @Argument Long id) {
        log.debug("courseUsers {}", id);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseUserService.getUsersByCourseId(id, authentication.getName());
    }

    @QueryMapping(name = "searchCourseUsersByUsername")
    public List<GetCourseUserOutput> searchCourseUsersByUsername(Authentication authentication, @Argument SearchCourseUsersByUsernameInput searchCourseUsersByUsernameInput) {
        Long courseId = searchCourseUsersByUsernameInput.courseId();
        String username = searchCourseUsersByUsernameInput.username();
        log.debug("searchCourseUsersByUsername courseId = {}, username = {}", courseId, username);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseUserService.searchByCourseIdAndUsername(courseId, username, authentication.getName());
    }

    @QueryMapping(name = "usersNotInCourse")
    public List<GetUserOutput> getUsersNotInCourseByCourseId(Authentication authentication, @Argument Long id) {
        log.debug("usersNotInCourse {}", id);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseUserService.getUsersNotInCourseByCourseId(id, authentication.getName());
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

    @MutationMapping(name = "createCourseUser")
    public GetCourseUserOutput createCourseUser(Authentication authentication, @Argument CreateCourseUserInput courseUserInput) {
        log.debug("createCourse: {}", courseUserInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return courseUserService.createCourseUser(courseUserInput.courseId(), authentication.getName(), courseUserInput.username(), courseUserInput.roles());
    }

    @MutationMapping(name = "deleteCourseUser")
    public Boolean deleteAnnouncement(Authentication authentication, @Argument Long courseId, @Argument String userId) {
        if (!authentication.isAuthenticated()) {
            return null;
        }

        log.debug("deleteAnnouncement: courseId = {}, userId = {}", courseId, userId);
        return courseUserService.deleteCourseUser(courseId, userId, authentication.getName());
    }
}
