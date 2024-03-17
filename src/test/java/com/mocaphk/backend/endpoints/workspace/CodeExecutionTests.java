package com.mocaphk.backend.endpoints.workspace;

import com.mocaphk.backend.endpoints.mocap.workspace.model.AttemptResult;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CodeExecutionResult;
import com.mocaphk.backend.endpoints.mocap.workspace.service.CodeExecutionService;
import com.mocaphk.backend.enums.CodeStream;
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
    private static final String TEST_STUDENT_USER_ID = "2590f78f-ac87-4dc1-ba3a-d8ffb7189c39";

    @Test
    public void testRunAttempt() throws IOException {
        AttemptResult result = codeExecutionService.runAttempt(TEST_STUDENT_USER_ID, 1L);
        assertThat(result.getAttempt().getId()).isEqualTo(1L);
        assertThat(result.getResults().size()).isEqualTo(1);

        CodeExecutionResult codeExecutionResult = result.getResults().get(0);
        assertThat(codeExecutionResult.getAttemptResultId()).isEqualTo(result.getId());
        assertThat(codeExecutionResult.getOutput().size()).isEqualTo(1);
        assertThat(codeExecutionResult.getIsExecutionSuccess()).isTrue();
        assertThat(codeExecutionResult.getIsCorrect()).isTrue();
        assertThat(codeExecutionResult.getIsExceedTimeLimit()).isFalse();

        CodeExecutionResult.CodeExecutionOutput output = codeExecutionResult.getOutput().get(0);
        assertThat(output.getStreamType()).isEqualTo(CodeStream.STDOUT);
        assertThat(output.getPayload()).isEqualTo("123 Hello World!\n");
    }
}
