package com.mocaphk.backend.endpoints.mocap.workspace.controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;
import com.mocaphk.backend.endpoints.mocap.workspace.model.AttemptResult;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CodeExecutionResult;
import com.mocaphk.backend.endpoints.mocap.workspace.service.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CodeExecutionController {
    private final CodeExecutionService codeExecutionService;

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return new ResponseEntity<>("IOException: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @QueryMapping(name = "runAttempt")
    public AttemptResult runAttempt(@Argument Long attemptId) throws IOException {
        log.debug("runAttempt: {}", attemptId);
        return codeExecutionService.runAttempt(attemptId);
    }

    @QueryMapping(name = "runTestcase")
    public CodeExecutionResult runTestcase(@Argument Long attemptId, @Argument Long testcaseId) throws IOException {
        log.debug("runTestcase: {}, {}", attemptId, testcaseId);
        return codeExecutionService.runTestcase(attemptId, testcaseId);
    }

    @QueryMapping(name = "runTestcaseWithCode")
    public CodeExecutionResult runTestcaseWithCode(@Argument Long questionId, @Argument Long testcaseId, @Argument String code) throws IOException {
        log.debug("runTestcaseWithCode: {}, {}, {}", questionId, testcaseId, code);
        return codeExecutionService.runTestcaseWithCode(questionId, testcaseId, code);
    }

    @MutationMapping(name = "runAllTestcasesWithCode")
    public AttemptResult runAllTestcasesWithCode(@Argument Long questionId, @Argument String code) throws IOException {
        log.debug("runAllTestcasesWithCode: {}, {}", questionId, code);
        return codeExecutionService.runTestcasesWithCode(questionId, code);
    }

    @MutationMapping(name = "submitAttempt")
    public AttemptResult submitAttempt(@Argument Long attemptId) throws IOException {
        log.debug("submitAttempt: {}", attemptId);
        return codeExecutionService.submitAttempt(attemptId);
    }
}
