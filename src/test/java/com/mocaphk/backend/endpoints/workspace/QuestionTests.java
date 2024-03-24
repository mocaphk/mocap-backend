package com.mocaphk.backend.endpoints.workspace;

import com.mocaphk.backend.endpoints.mocap.workspace.controller.QuestionController;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.ProgrammingLanguage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class QuestionTests {
    @Autowired
    private QuestionController questionController;

    @Test
    public void testCreateQuestion() {
        Question question = questionController.createQuestion(
                new CreateQuestionInput(
                        "Test Question",
                        "Python Test Question",
                        ProgrammingLanguage.PYTHON,
                        "print('Hello World!')",
                        CheckingMethod.CONSOLE,
                        1L,
                        "python {mainFile}",
                        1000,
                        1L
                )
        );
        assertThat(question.getId()).isNotNull();
    }

    @Test
    public void testGetQuestionById() {
        Question question = questionController.getQuestionById(1L);
        assertThat(question.getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateQuestion() {
        Question question = questionController.updateQuestion(
                1L,
                new UpdateQuestionInput(
                        null,
                        "Python Test Question 1",
                        ProgrammingLanguage.PYTHON,
                        "print('123 Hello World!')",
                        CheckingMethod.CONSOLE,
                        null,
                        null,
                        null,
                        null
                )
        );
        assertThat(question.getId()).isEqualTo(1L);
    }

    @Test
    public void testDeleteQuestion() {
        Question create = questionController.createQuestion(
                new CreateQuestionInput(
                        "Test Question",
                        "Python Test Question",
                        ProgrammingLanguage.PYTHON,
                        "print('Hello World!')",
                        CheckingMethod.CONSOLE,
                        1L,
                        "python {mainFile}",
                        1000,
                        1L
                )
        );
        Question question = questionController.deleteQuestion(create.getId());
        assertThat(question.getId()).isEqualTo(create.getId());
    }
}
