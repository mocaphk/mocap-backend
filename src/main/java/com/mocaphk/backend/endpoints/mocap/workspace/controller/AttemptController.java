package com.mocaphk.backend.endpoints.mocap.workspace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;
import com.mocaphk.backend.endpoints.mocap.workspace.service.AttemptService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AttemptController {
    private final AttemptService attemptService;

    @QueryMapping(name = "attempt")
    public Attempt getAttemptById(@Argument Long attemptId) {
        log.debug("getAttemptById: {}", attemptId);
        return attemptService.getAttemptById(attemptId);
    }

    @QueryMapping(name = "attempts")
    public List<Attempt> getAttemptsByQuestionId(Authentication authentication, @Argument Long questionId) {
        log.debug("getAttemptsByQuestionId: {}", questionId);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return attemptService.getAttemptsByQuestionId(authentication.getName() , questionId);
    }

    @MutationMapping(name = "createAttempt")
    public Attempt createAttempt(Authentication authentication, @Argument CreateAttemptInput attemptInput) {
        log.debug("createAttempt: {}", attemptInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return createAttempt(authentication.getName(), attemptInput);
    }

    public Attempt createAttempt(String userId, CreateAttemptInput attemptInput) {
        return attemptService.createAttempt(userId, attemptInput);
    }

    @MutationMapping(name = "updateAttempt")
    public Attempt updateAttempt(@Argument Long attemptId, @Argument UpdateAttemptInput attemptInput) {
        log.debug("updateAttempt: {}, {}", attemptId, attemptInput);
        return attemptService.updateAttempt(attemptId, attemptInput);
    }
}
