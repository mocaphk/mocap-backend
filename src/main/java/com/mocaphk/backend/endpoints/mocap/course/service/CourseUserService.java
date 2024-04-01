package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import com.mocaphk.backend.endpoints.keycloak.user.repository.UserRepository;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import com.mocaphk.backend.endpoints.mocap.course.model.CourseUser;
import com.mocaphk.backend.endpoints.mocap.course.model.CourseUserId;
import com.mocaphk.backend.endpoints.mocap.course.model.CourseUserRole;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.enums.CourseRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseUserService {
    private final CourseUserRepository courseUserRepository;
    private final UserRepository userRepository;

    public List<CourseUserRole> getUserRoles(Long courseId, String userId) {
        CourseUserId id = new CourseUserId(courseId, userId);
        CourseUser courseUser = courseUserRepository.findById(id).orElse(null);

        if (courseUser == null) {
            return null;
        }

        return courseUser.getRoles();
    }

    public List<Course> getCoursesByUserId(String userId) {
        List<CourseUser> courseUsers = courseUserRepository.findByUserId(userId);
        List<Course> courses = courseUsers.stream().map(CourseUser::getCourse).toList();
        courses.forEach(course -> {
            List<MocapUser> admins = courseUserRepository.findByCourseIdAndRole(course.getId(), CourseRole.ADMIN).stream().map(CourseUser::getUser).toList();
            List<MocapUser> lecturers = courseUserRepository.findByCourseIdAndRole(course.getId(), CourseRole.LECTURER).stream().map(CourseUser::getUser).toList();
            List<MocapUser> tutors = courseUserRepository.findByCourseIdAndRole(course.getId(), CourseRole.TUTOR).stream().map(CourseUser::getUser).toList();
            List<MocapUser> students = courseUserRepository.findByCourseIdAndRole(course.getId(), CourseRole.STUDENT).stream().map(CourseUser::getUser).toList();
            course.setAdmins(admins);
            course.setLecturers(lecturers);
            course.setTutors(tutors);
            course.setStudents(students);
        });
        return courses;
    }
}
