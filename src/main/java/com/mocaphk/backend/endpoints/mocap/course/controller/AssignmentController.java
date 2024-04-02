package com.mocaphk.backend.endpoints.mocap.course.controller;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateAssignmentInput;
import com.mocaphk.backend.endpoints.mocap.course.dto.CreateCourseInput;
import com.mocaphk.backend.endpoints.mocap.course.model.Assignment;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import com.mocaphk.backend.endpoints.mocap.course.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @MutationMapping(name = "createAssignment")
    public Assignment createAssignment(Authentication authentication, @Argument CreateAssignmentInput assignmentInput) {
        log.debug("createAssignment: {}", assignmentInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return assignmentService.createAssignment(assignmentInput, authentication.getName());
    }
}
