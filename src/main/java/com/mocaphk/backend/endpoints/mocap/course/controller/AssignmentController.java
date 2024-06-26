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
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @QueryMapping(name = "assignment")
    public Assignment getAssignment(Authentication authentication, @Argument Long id) {
        log.debug("getAssignment: {}", id);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return assignmentService.getAssignmentById(id, authentication.getName());
    }

    @QueryMapping(name = "assignmentsBetween")
    public List<Assignment> getAssignmentsBetween(Authentication authentication, @Argument String startDate, @Argument String endDate) {
        log.debug("getAssignmentsBetween: {}, {}", startDate, endDate);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return assignmentService.getAssignmentsBetween(authentication.getName(), startDate, endDate);
    }

    @MutationMapping(name = "createAssignment")
    public Assignment createAssignment(Authentication authentication, @Argument CreateAssignmentInput assignmentInput) {
        log.debug("createAssignment: {}", assignmentInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return assignmentService.createAssignment(assignmentInput, authentication.getName());
    }

    @SchemaMapping
    public Integer completion(Assignment assignment, Authentication authentication) {
        log.debug("Assignment completion: assignment = {}, authentication = {}", assignment, authentication);
        return assignmentService.getCompletion(authentication.getName(), assignment);
    }
}
