package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateAssignmentInput;
import com.mocaphk.backend.endpoints.mocap.course.dto.CreateCourseInput;
import com.mocaphk.backend.endpoints.mocap.course.model.*;
import com.mocaphk.backend.endpoints.mocap.course.repository.AssignmentRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.enums.CourseRole;
import com.mocaphk.backend.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final MocapUserRepository mocapUserRepository;
    private final CourseUserRepository courseUserRepository;

    public Assignment getAssignmentById(Long id, String userId) {
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) {
            return null;
        }

        // Check if user is in the course or not
        Course course = assignment.getCourse();
        if (course == null) {
            return null;
        }

        CourseUserId courseUserId = new CourseUserId(course.getId(), userId);
        CourseUser courseUser = courseUserRepository.findById(courseUserId).orElse(null);

        if (courseUser == null) {
            return null;
        }

        return assignment;
    }

    public Assignment createAssignment(CreateAssignmentInput input, String userId) {
        MocapUser user = mocapUserRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        Course course = courseRepository.findById(input.courseId()).orElse(null);

        if (course == null) {
            return null;
        }

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(input.title());
        assignment.setDescription(input.description());
        assignment.setType(input.type());
        assignment.setDateDue(input.dateDue());
        assignment.setDateOpen(input.dateOpen());
        assignment.setDateClose(input.dateClose());
        assignment.setCreatedBy(user);
        assignment.setQuestions(new ArrayList<Question>());
        String now = DateUtils.now();
        assignment.setCreatedAt(now);
        assignment.setUpdatedAt(now);

        assignmentRepository.save(assignment);

        return assignment;
    }
}
