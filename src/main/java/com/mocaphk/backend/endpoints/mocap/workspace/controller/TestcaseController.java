package com.mocaphk.backend.endpoints.mocap.workspace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CustomTestcase;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Testcase;
import com.mocaphk.backend.endpoints.mocap.workspace.service.TestcaseService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestcaseController {
    private final TestcaseService testcaseService;

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

    @MutationMapping(name = "createTestcases")
    public List<Testcase> createTestcases(@Argument Long questionId, @Argument List<CreateTestcaseInput> inputs) {
        log.debug("createTestcases: {}, {}", questionId, inputs);
        return testcaseService.createTestcases(questionId, inputs);
    }

    @MutationMapping(name = "createCustomTestcases")
    public List<CustomTestcase> createCustomTestcases(Authentication authentication, @Argument Long questionId, @Argument List<CreateCustomTestcaseInput> inputs) {
        log.debug("createCustomTestcases: {}, {}", questionId, inputs);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return createCustomTestcases(authentication.getName(), questionId, inputs);
    }

    public List<CustomTestcase> createCustomTestcases(String userId, Long questionId, List<CreateCustomTestcaseInput> inputs) {
        return testcaseService.createCustomTestcases(userId, questionId, inputs);
    }

    @MutationMapping(name = "updateTestcase")
    public Testcase updateTestcase(@Argument Long id, @Argument UpdateTestcaseInput input) {
        log.debug("updateTestcase: {}, {}", id, input);
        return testcaseService.updateTestcase(id, input);
    }

    @MutationMapping(name = "updateCustomTestcase")
    public CustomTestcase updateCustomTestcase(@Argument Long id, @Argument UpdateCustomTestcaseInput input) {
        log.debug("updateCustomTestcase: {}, {}", id, input);
        return testcaseService.updateCustomTestcase(id, input);
    }

    @MutationMapping(name = "deleteTestcase")
    public Testcase deleteTestcase(@Argument Long id) {
        log.debug("deleteTestcase: {}", id);
        return testcaseService.deleteTestcase(id);
    }

    @MutationMapping(name = "deleteCustomTestcase")
    public CustomTestcase deleteCustomTestcase(@Argument Long id) {
        log.debug("deleteCustomTestcase: {}", id);
        return testcaseService.deleteCustomTestcase(id);
    }
}
