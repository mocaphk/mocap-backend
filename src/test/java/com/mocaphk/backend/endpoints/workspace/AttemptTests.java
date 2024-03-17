package com.mocaphk.backend.endpoints.workspace;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mocaphk.backend.endpoints.mocap.workspace.controller.AttemptController;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateAttemptInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Attempt;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class AttemptTests {
    @Autowired
    private AttemptController attemptController;
    private static final String TEST_STUDENT_USER_ID = "2590f78f-ac87-4dc1-ba3a-d8ffb7189c39";

    @Test
    public void testCreateAttempt() {
        Attempt attempt = attemptController.createAttempt(
                TEST_STUDENT_USER_ID,
                new CreateAttemptInput(
                        1L,
                        "print('Hello World!')"
                )
        );
        assertThat(attempt.getId()).isNotNull();
    }

    @Test
    public void testGetAttemptById() {
        Attempt attempt = attemptController.getAttemptById(1L);
        assertThat(attempt.getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateAttempt() {
        Attempt attempt = attemptController.updateAttempt(
                1L,
                new UpdateAttemptInput(
                        "print('123 Hello World!')"
                )
        );
        assertThat(attempt.getId()).isEqualTo(1L);
    }
}
