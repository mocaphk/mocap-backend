package com.mocaphk.backend.endpoints.workspace;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mocaphk.backend.endpoints.mocap.workspace.controller.TestcaseController;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CustomTestcase;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Testcase;
import com.mocaphk.backend.endpoints.mocap.workspace.model.TestcaseInputEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class TestcaseTests {
    @Autowired
    private TestcaseController testcaseController;
    private static final String TEST_STUDENT_USER_ID = "2590f78f-ac87-4dc1-ba3a-d8ffb7189c39";

    @Test
    public void testCreateTestcases() {
         List<Testcase> testcases = testcaseController.createTestcases(
                 1L,
                 List.of(
                         new CreateTestcaseInput(
                                 List.of(new TestcaseInputEntryInput("foo", "bar")),
                                 null,
                                 false
                         )
                 )
         );
        assertThat(testcases.get(0).getId()).isNotNull();
        assertThat(testcases.get(0).getQuestionId()).isEqualTo(1L);
    }

    @Test
    public void testCreateCustomTestcases() {
        List<CustomTestcase> testcases = testcaseController.createCustomTestcases(
                TEST_STUDENT_USER_ID,
                1L,
                List.of(
                        new CreateCustomTestcaseInput(
                                List.of(new TestcaseInputEntryInput("foo", "bar"))
                        )
                )
        );
        assertThat(testcases.get(0).getId()).isNotNull();
    }

    @Test
    public void testGetTestcaseById() {
        Testcase testcase = testcaseController.getTestcaseById(1L);
        assertThat(testcase.getId()).isEqualTo(1L);
    }

    @Test
    public void testGetCustomTestcaseById() {
        CustomTestcase testcase = testcaseController.getCustomTestcaseById(3L);
        assertThat(testcase.getId()).isEqualTo(3L);
    }

    @Test
    public void testGetTestcasesByQuestionId() {
        List<Testcase> testcases = testcaseController.getTestcasesByQuestionId(1L);
        assertThat(testcases.size()).isGreaterThan(0);
    }

    @Test
    public void testGetCustomTestcasesByQuestionId() {
        List<CustomTestcase> testcases = testcaseController.getCustomTestcasesByQuestionId(TEST_STUDENT_USER_ID, 1L);
        assertThat(testcases.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateTestcase() {
        Testcase testcase = testcaseController.updateTestcase(
                1L,
                new UpdateTestcaseInput(
                        List.of(new TestcaseInputEntryInput("foo2", "bar2")),
                        null,
                        false
                )
        );
        assertThat(testcase.getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateCustomTestcase() {
        CustomTestcase testcase = testcaseController.updateCustomTestcase(
                3L,
                new UpdateCustomTestcaseInput(
                        List.of(new TestcaseInputEntryInput("foo2", "bar2"))
                )
        );
        assertThat(testcase.getId()).isEqualTo(3L);
    }

    @Test
    public void testDeleteTestcase() {
        Testcase testcase = testcaseController.deleteTestcase(5L);
        assertThat(testcase.getId()).isEqualTo(5L);
    }

    @Test
    public void testDeleteCustomTestcase() {
        CustomTestcase testcase = testcaseController.deleteCustomTestcase(3L);
        assertThat(testcase.getId()).isEqualTo(3L);
    }
}
