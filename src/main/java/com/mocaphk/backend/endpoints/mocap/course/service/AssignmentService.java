package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.components.MocapMailSender;
import com.mocaphk.backend.endpoints.mocap.course.dto.CreateAssignmentInput;
import com.mocaphk.backend.endpoints.mocap.course.model.*;
import com.mocaphk.backend.endpoints.mocap.course.repository.AssignmentRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.service.MocapUserService;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.endpoints.mocap.workspace.service.AttemptService;
import com.mocaphk.backend.enums.CourseRole;
import com.mocaphk.backend.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final MocapUserRepository mocapUserRepository;
    private final CourseUserRepository courseUserRepository;
    private final MocapUserService mocapUserService;
    private final AttemptService attemptService;
    private final CourseUserService courseUserService;
    private final MocapMailSender mocapMailSender;

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

    public List<Assignment> getAssignmentsBetween(String userId, String start, String end) {
        List<Course> courses = courseUserService.getCoursesByUserId(userId);
        List<Assignment> assignments = new ArrayList<>();
        for (Course course : courses) {
            assignments.addAll(assignmentRepository.findByCourseIdAndDateDueBetween(course.getId(), start, end));
        }
        return assignments.stream().sorted(Comparator.comparing(Assignment::getDateDue)).toList();
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

        scheduleAssignmentMail(assignment);
        assignmentRepository.save(assignment);

        return assignment;
    }

    private void scheduleAssignmentMail(Assignment assignment) {
        String subject = String.format("[MOCAP] %s: New Assignment: %s",
                assignment.getCourse().getCode(),
                assignment.getTitle()
        );
        String body = String.format("New assignment:\n%s\n\nDescription:\n%s\n\nDue date: %s",
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDateDue()
        );

        for (MocapUser user : mocapUserService.getMocapUsersByCourseId(assignment.getCourse().getId())) {
            mocapMailSender.scheduleMail(
                    "MOCAP",
                    user.getEmail(),
                    subject,
                    body,
                    DateUtils.parse(assignment.getDateOpen())
            );
        }
    }
    
    public Integer getCompletion(String userId, Assignment assignment) {
        if (assignment.getQuestions().isEmpty()) {
            return 100;
        }
        int total = 0;
        int completed = 0;
        for (Question question : assignment.getQuestions()) {
            if (attemptService.getLatestSubmissionByQuestionId(userId, question.getId()) != null) {
                completed++;
            }
            total++;
        }
        return (int) Math.round((double) completed / total * 100);
    }
}
