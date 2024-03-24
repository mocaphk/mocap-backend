package com.mocaphk.backend.endpoints.workspace;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.*;
import com.mocaphk.backend.endpoints.mocap.workspace.model.*;
import com.mocaphk.backend.endpoints.mocap.workspace.service.*;
import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.CodeStream;
import com.mocaphk.backend.enums.ProgrammingLanguage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mocaphk.backend.endpoints.mocap.workspace.model.CodeExecutionResult.combineOutput;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
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
    @Autowired
    private TestcaseService testcaseService;

    private static final String TEST_STUDENT_USER_ID = "2590f78f-ac87-4dc1-ba3a-d8ffb7189c39";

    public Question createTestQuestion(String sampleCode) {
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
                        sampleCode,
                        CheckingMethod.CONSOLE,
                        codingEnvironment.getId(),
                        "python {mainFile}",
                        1000,
                        // TODO: remove this hard-coded value
                        1L
                )
        );
        assertThat(question.getId()).isNotNull();
        return question;
    }

    @Test
    public void testRunAttemptWithoutTestcase() throws IOException {
        Question question = createTestQuestion("print('Hello World!')");

        Attempt attempt = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "print('Hello World!')"
                )
        );
        assertThat(attempt.getId()).isNotNull();

        AttemptResult result1 = codeExecutionService.runAttempt(attempt.getId());
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

        attemptService.updateAttempt(
                attempt.getId(),
                new UpdateAttemptInput(
                        "print('Bye World!')"
                )
        );

        AttemptResult result2 = codeExecutionService.runAttempt(attempt.getId());
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

    @Test
    public void testRunAttemptWithTestcases() throws IOException {
        Question question = createTestQuestion("x = input(\"Enter a number: \")\nprint(\"You entered:\", x)\n");
        testcaseService.createTestcases(
                question.getId(),
                List.of(
                        new CreateTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "123")),
                                null,
                                false
                        ),
                        new CreateTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "456")),
                                null,
                                true
                        )
                )
        );

        testcaseService.createCustomTestcases(
                TEST_STUDENT_USER_ID,
                question.getId(),
                List.of(
                        new CreateCustomTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "789"))
                        ),
                        new CreateCustomTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "101112"))
                        )
                )
        );

        Attempt attempt = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "x = input(\"Enter a number: \")\nprint(\"You entered:\", x)\n"
                )
        );
        assertThat(attempt.getId()).isNotNull();

        AttemptResult result1 = codeExecutionService.runAttempt(attempt.getId());
        assertThat(result1.getResults().size()).isEqualTo(4);

        List<String> outputs = new ArrayList<>();
        for (CodeExecutionResult codeExecutionResult : result1.getResults()) {
            assertThat(codeExecutionResult.getAttemptResultId()).isEqualTo(result1.getId());
            // async execution with stdin, so the number of output fragments created is not guaranteed
            assertThat(codeExecutionResult.getOutput().size()).satisfiesAnyOf(
                    res -> assertThat(res).isEqualTo(1),
                    res -> assertThat(res).isEqualTo(2)
            );
            assertThat(codeExecutionResult.getIsExecutionSuccess()).isTrue();
            assertThat(codeExecutionResult.getIsCorrect()).isTrue();
            assertThat(codeExecutionResult.getIsExceedTimeLimit()).isFalse();

            for (CodeExecutionResult.CodeExecutionOutput output : codeExecutionResult.getOutput()) {
                assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
            }
            outputs.add(combineOutput(codeExecutionResult.getOutput()));
        }
        assertThat(outputs).containsExactly(
                "Enter a number: You entered: 123\n",
                "Enter a number: You entered: 456\n",
                "Enter a number: You entered: 789\n",
                "Enter a number: You entered: 101112\n"
        );

        attemptService.updateAttempt(
                attempt.getId(),
                new UpdateAttemptInput(
                        "print('Bye World!')"
                )
        );

        AttemptResult result2 = codeExecutionService.runAttempt(attempt.getId());
        assertThat(result2.getResults().size()).isEqualTo(4);

        for (CodeExecutionResult codeExecutionResult : result2.getResults()) {
            assertThat(codeExecutionResult.getAttemptResultId()).isEqualTo(result2.getId());
            assertThat(codeExecutionResult.getOutput().size()).isEqualTo(1);
            assertThat(codeExecutionResult.getIsExecutionSuccess()).isTrue();
            assertThat(codeExecutionResult.getIsCorrect()).isFalse();
            assertThat(codeExecutionResult.getIsExceedTimeLimit()).isFalse();

            CodeExecutionResult.CodeExecutionOutput output = codeExecutionResult.getOutput().get(0);
            assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
            assertThat(output.getPayload()).isEqualTo("Bye World!\n");
        }
    }

    @Test
    public void testRunTestcase() throws IOException {
        Question question = createTestQuestion("x = input(\"Enter a number: \")\nprint(\"You entered:\", x)\n");
        List<Testcase> testcases = testcaseService.createTestcases(
                question.getId(),
                List.of(
                        new CreateTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "123")),
                                null,
                                false
                        ),
                        new CreateTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "456")),
                                null,
                                true
                        )
                )
        );

        Attempt attempt = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "x = input(\"Enter a number: \")\nprint(\"You entered:\", x)\n"
                )
        );
        assertThat(attempt.getId()).isNotNull();

        CodeExecutionResult result1 = codeExecutionService.runTestcase(attempt.getId(), testcases.get(0).getId());

        // async execution with stdin, so the number of output fragments created is not guaranteed
        assertThat(result1.getOutput().size()).satisfiesAnyOf(
                res -> assertThat(res).isEqualTo(1),
                res -> assertThat(res).isEqualTo(2)
        );
        assertThat(result1.getIsExecutionSuccess()).isTrue();
        assertThat(result1.getIsCorrect()).isTrue();
        assertThat(result1.getIsExceedTimeLimit()).isFalse();

        for (CodeExecutionResult.CodeExecutionOutput output : result1.getOutput()) {
            assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
        }
        assertThat(combineOutput(result1.getOutput())).isEqualTo("Enter a number: You entered: 123\n");

        attemptService.updateAttempt(
                attempt.getId(),
                new UpdateAttemptInput(
                        "print('Bye World!')"
                )
        );

        CodeExecutionResult result2 = codeExecutionService.runTestcase(attempt.getId(), testcases.get(0).getId());

        assertThat(result2.getOutput().size()).isEqualTo(1);
        assertThat(result2.getIsExecutionSuccess()).isTrue();
        assertThat(result2.getIsCorrect()).isFalse();
        assertThat(result2.getIsExceedTimeLimit()).isFalse();

        CodeExecutionResult.CodeExecutionOutput output = result2.getOutput().get(0);
        assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
        assertThat(output.getPayload()).isEqualTo("Bye World!\n");
    }

    @Test
    public void testSubmitAttempt() throws IOException {
        Question question = createTestQuestion("x = input(\"Enter a number: \")\nprint(\"You entered:\", x)\n");
        testcaseService.createTestcases(
                question.getId(),
                List.of(
                        new CreateTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "123")),
                                null,
                                false
                        ),
                        new CreateTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "456")),
                                null,
                                true
                        )
                )
        );

        testcaseService.createCustomTestcases(
                TEST_STUDENT_USER_ID,
                question.getId(),
                List.of(
                        new CreateCustomTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "789"))
                        ),
                        new CreateCustomTestcaseInput(
                                List.of(new TestcaseInputEntryInput("x", "101112"))
                        )
                )
        );

        Attempt attempt1 = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "x = input(\"Enter a number: \")\nprint(\"You entered:\", x)\n"
                )
        );
        assertThat(attempt1.getId()).isNotNull();

        AttemptResult result1 = codeExecutionService.submitAttempt(attempt1.getId());
        assertThat(result1.getAttempt().getIsSubmitted()).isTrue();
        assertThat(result1.getResults().size()).isEqualTo(2);

        List<String> outputs = new ArrayList<>();
        for (CodeExecutionResult codeExecutionResult : result1.getResults()) {
            assertThat(codeExecutionResult.getAttemptResultId()).isEqualTo(result1.getId());
            // async execution with stdin, so the number of output fragments created is not guaranteed
            assertThat(codeExecutionResult.getOutput().size()).satisfiesAnyOf(
                    res -> assertThat(res).isEqualTo(1),
                    res -> assertThat(res).isEqualTo(2)
            );
            assertThat(codeExecutionResult.getIsExecutionSuccess()).isTrue();
            assertThat(codeExecutionResult.getIsCorrect()).isTrue();
            assertThat(codeExecutionResult.getIsExceedTimeLimit()).isFalse();

            for (CodeExecutionResult.CodeExecutionOutput output : codeExecutionResult.getOutput()) {
                assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
            }
            outputs.add(combineOutput(codeExecutionResult.getOutput()));
        }
        assertThat(outputs).containsExactly(
                "Enter a number: You entered: 123\n",
                "Enter a number: You entered: 456\n"
        );

        Attempt attempt2 = attemptService.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        question.getId(),
                        "print('Bye World!')"
                )
        );
        assertThat(attempt2.getId()).isNotNull();

        AttemptResult result2 = codeExecutionService.submitAttempt(attempt2.getId());
        assertThat(result2.getAttempt().getIsSubmitted()).isTrue();
        assertThat(result2.getResults().size()).isEqualTo(2);

        for (CodeExecutionResult codeExecutionResult : result2.getResults()) {
            assertThat(codeExecutionResult.getAttemptResultId()).isEqualTo(result2.getId());
            assertThat(codeExecutionResult.getOutput().size()).isEqualTo(1);
            assertThat(codeExecutionResult.getIsExecutionSuccess()).isTrue();
            assertThat(codeExecutionResult.getIsCorrect()).isFalse();
            assertThat(codeExecutionResult.getIsExceedTimeLimit()).isFalse();

            CodeExecutionResult.CodeExecutionOutput output = codeExecutionResult.getOutput().get(0);
            assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
            assertThat(output.getPayload()).isEqualTo("Bye World!\n");
        }
    }
}
