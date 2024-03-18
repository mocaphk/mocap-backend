package com.mocaphk.backend.endpoints.workspace;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.*;
import com.mocaphk.backend.endpoints.mocap.workspace.service.AttemptService;
import com.mocaphk.backend.endpoints.mocap.workspace.service.CodeExecutionService;
import com.mocaphk.backend.endpoints.mocap.workspace.service.CodingEnvironmentService;
import com.mocaphk.backend.endpoints.mocap.workspace.service.QuestionService;
import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.CodeStream;
import com.mocaphk.backend.enums.ProgrammingLanguage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class CodeExecutionTests {
    @Autowired
    private CodeExecutionService codeExecutionService;
    @Autowired
    private CodingEnvironmentService codingEnvironmentService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AttemptService attemptService;

    private static final String TEST_STUDENT_USER_ID = "2590f78f-ac87-4dc1-ba3a-d8ffb7189c39";

    @Test
    public void testRunAttempt() throws IOException {
        CodingEnvironment codingEnvironment = codingEnvironmentService.createCodingEnvironment(
                new CreateCodingEnvironmentInput(
                        "Test Coding Environment",
                        "Python Test Coding Environment",
                        "FROM python:3\n"
                )
        );
        assertThat(codingEnvironment.getId()).isNotNull();

        codingEnvironmentService.buildCodingEnvironment(codingEnvironment.getId());
        assertThat(codingEnvironment.getId()).isNotNull();

        Question question = questionService.createQuestion(
                new CreateQuestionInput(
                        "Test Question",
                        "Python Test Question",
                        ProgrammingLanguage.PYTHON,
                        "print('Hello World!')",
                        CheckingMethod.CONSOLE,
                        codingEnvironment.getId(),
                        "python {mainFile}",
                        1000,
                        // TODO: remove this hard-coded value
                        1L
                )
        );
        assertThat(question.getId()).isNotNull();

        Attempt attempt1 = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "print('Hello World!')"
                )
        );
        assertThat(attempt1.getId()).isNotNull();

        AttemptResult result1 = codeExecutionService.runAttempt(attempt1.getId());
        assertThat(result1.getResults().size()).isEqualTo(1);

        CodeExecutionResult codeExecutionResult1 = result1.getResults().get(0);
        assertThat(codeExecutionResult1.getAttemptResultId()).isEqualTo(result1.getId());
        assertThat(codeExecutionResult1.getOutput().size()).isEqualTo(1);
        assertThat(codeExecutionResult1.getIsExecutionSuccess()).isTrue();
        assertThat(codeExecutionResult1.getIsCorrect()).isTrue();
        assertThat(codeExecutionResult1.getIsExceedTimeLimit()).isFalse();

        CodeExecutionResult.CodeExecutionOutput output1 = codeExecutionResult1.getOutput().get(0);
        assertThat(output1.getStreamType()).isEqualTo(CodeStream.STDOUT);
        assertThat(output1.getPayload()).isEqualTo("Hello World!\n");

        Attempt attempt2 = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "print('Bye World!')"
                )
        );
        assertThat(attempt2.getId()).isNotNull();

        AttemptResult result2 = codeExecutionService.runAttempt(attempt2.getId());
        assertThat(result2.getResults().size()).isEqualTo(1);

        CodeExecutionResult codeExecutionResult2 = result2.getResults().get(0);
        assertThat(codeExecutionResult2.getAttemptResultId()).isEqualTo(result2.getId());
        assertThat(codeExecutionResult2.getOutput().size()).isEqualTo(1);
        assertThat(codeExecutionResult2.getIsExecutionSuccess()).isTrue();
        assertThat(codeExecutionResult2.getIsCorrect()).isFalse();
        assertThat(codeExecutionResult2.getIsExceedTimeLimit()).isFalse();

        CodeExecutionResult.CodeExecutionOutput output2 = codeExecutionResult2.getOutput().get(0);
        assertThat(output2.getStreamType()).isEqualTo(CodeStream.STDOUT);
        assertThat(output2.getPayload()).isEqualTo("Bye World!\n");
    }
}
