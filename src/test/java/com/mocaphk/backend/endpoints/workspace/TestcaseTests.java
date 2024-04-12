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
    public void testCreateAndUpdateTestcases() {
         List<Testcase> testcases = testcaseController.createAndUpdateTestcases(
                 1L,
                 List.of(
                         new CreateAndUpdateTestcaseInput(
                                 null,
                                 List.of(new TestcaseInputEntryInput("foo", "bar")),
                                 null,
                                 false
                         ),
                         new CreateAndUpdateTestcaseInput(
                                1L,
                                List.of(new TestcaseInputEntryInput("foo2", "bar2")),
                                null,
                                false
                         )
                 )
         );
        assertThat(testcases.get(0).getId()).isNotNull();
        assertThat(testcases.get(0).getQuestionId()).isEqualTo(1L);
        assertThat(testcases.get(1).getId()).isEqualTo(1L);
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
        CustomTestcase testcase = testcaseController.getCustomTestcaseById(2L);
        assertThat(testcase.getId()).isEqualTo(2L);
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
    public void testUpdateCustomTestcase() {
        CustomTestcase testcase = testcaseController.updateCustomTestcase(
                2L,
                new UpdateCustomTestcaseInput(
                        List.of(new TestcaseInputEntryInput("foo2", "bar2"))
                )
        );
        assertThat(testcase.getId()).isEqualTo(2L);
    }

    @Test
    public void testDeleteTestcase() {
        List<Testcase> testcases = testcaseController.createAndUpdateTestcases(
                1L,
                List.of(
                        new CreateAndUpdateTestcaseInput(
                                null,
                                List.of(new TestcaseInputEntryInput("foo", "bar")),
                                null,
                                false
                        )
                )
        );
        for (Testcase testcase : testcases) {
            Testcase deleted = testcaseController.deleteTestcase(testcase.getId());
            assertThat(deleted.getId()).isEqualTo(testcase.getId());
        }
    }

    @Test
    public void testDeleteCustomTestcase() {
        List<CustomTestcase> testcases = testcaseController.createCustomTestcases(
                TEST_STUDENT_USER_ID,
                1L,
                List.of(
                        new CreateCustomTestcaseInput(
                                List.of(new TestcaseInputEntryInput("foo", "bar"))
                        )
                )
        );
        for (CustomTestcase testcase : testcases) {
            CustomTestcase deleted = testcaseController.deleteCustomTestcase(testcase.getId());
            assertThat(deleted.getId()).isEqualTo(testcase.getId());
        }
    }
}
