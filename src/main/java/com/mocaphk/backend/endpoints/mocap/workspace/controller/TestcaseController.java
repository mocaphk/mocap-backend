package com.mocaphk.backend.endpoints.mocap.workspace.controller;

import com.mocaphk.backend.endpoints.mocap.workspace.repository.CodeExecutionResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAndUpdateTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CustomTestcase;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Testcase;
import com.mocaphk.backend.endpoints.mocap.workspace.service.TestcaseService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestcaseController {
    private final TestcaseService testcaseService;
    private final CodeExecutionResultRepository codeExecutionResultRepository;

    @QueryMapping(name = "testcase")
    public Testcase getTestcaseById(@Argument Long id) {
        log.debug("getTestcaseById: {}", id);
        return testcaseService.getTestcaseById(id);
    }

    @QueryMapping(name = "customTestcase")
    public CustomTestcase getCustomTestcaseById(@Argument Long id) {
        log.debug("getCustomTestcaseById: {}", id);
        return testcaseService.getCustomTestcaseById(id);
    }

    @QueryMapping(name = "testcases")
    public List<Testcase> getTestcasesByQuestionId(@Argument Long questionId) {
        log.debug("getTestcasesByQuestionId: {}", questionId);
        return testcaseService.getTestcasesByQuestionId(questionId);
    }

    @QueryMapping(name = "customTestcases")
    public List<CustomTestcase> getCustomTestcasesByQuestionId(Authentication authentication, @Argument Long questionId) {
        log.debug("getCustomTestcasesByQuestionId: {}", questionId);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return getCustomTestcasesByQuestionId(authentication.getName(), questionId);
    }

    public List<CustomTestcase> getCustomTestcasesByQuestionId(String userId, Long questionId) {
        return testcaseService.getCustomTestcasesByQuestionId(userId, questionId);
    }

    @MutationMapping(name = "createAndUpdateTestcases")
    public List<Testcase> createAndUpdateTestcases(@Argument Long questionId, @Argument List<CreateAndUpdateTestcaseInput> testcaseInput) {
        log.debug("createAndUpdateTestcases: {}, {}", questionId, testcaseInput);
        return testcaseService.createAndUpdateTestcases(questionId, testcaseInput);
    }

    @MutationMapping(name = "createCustomTestcases")
    public List<CustomTestcase> createCustomTestcases(Authentication authentication, @Argument Long questionId, @Argument List<CreateCustomTestcaseInput> testcaseInput) {
        log.debug("createCustomTestcases: {}, {}", questionId, testcaseInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return createCustomTestcases(authentication.getName(), questionId, testcaseInput);
    }

    public List<CustomTestcase> createCustomTestcases(String userId, Long questionId, List<CreateCustomTestcaseInput> testcaseInput) {
        return testcaseService.createCustomTestcases(userId, questionId, testcaseInput);
    }

    @MutationMapping(name = "updateCustomTestcase")
    public CustomTestcase updateCustomTestcase(@Argument Long testcaseId, @Argument UpdateCustomTestcaseInput testcaseInput) {
        log.debug("updateCustomTestcase: {}, {}", testcaseId, testcaseInput);
        return testcaseService.updateCustomTestcase(testcaseId, testcaseInput);
    }

    @MutationMapping(name = "deleteTestcase")
    public Testcase deleteTestcase(@Argument Long testcaseId) {
        log.debug("deleteTestcase: {}", testcaseId);
        return testcaseService.deleteTestcase(testcaseId);
    }

    @MutationMapping(name = "deleteCustomTestcase")
    public CustomTestcase deleteCustomTestcase(@Argument Long testcaseId) {
        log.debug("deleteCustomTestcase: {}", testcaseId);
        return testcaseService.deleteCustomTestcase(testcaseId);
    }
}
