package com.mocaphk.backend.endpoints.workspace;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mocaphk.backend.endpoints.mocap.workspace.controller.CodingEnvironmentController;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CodingEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class CodingEnvironmentTests {
    @Autowired
    private CodingEnvironmentController codingEnvironmentController;

    @Test
    public void testCreateCodingEnvironment() {
        CodingEnvironment codingEnvironment = codingEnvironmentController.createCodingEnvironment(
                new CreateCodingEnvironmentInput(
                        "Test Coding Environment",
                        "Python Test Coding Environment",
                        "FROM python:3\n"
                )
        );
        assertThat(codingEnvironment.getId()).isNotNull();
    }

    @Test
    public void testGetCodingEnvironmentById() {
        CodingEnvironment codingEnvironment = codingEnvironmentController.getCodingEnvironmentById(1L);
        assertThat(codingEnvironment.getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateCodingEnvironment() {
        CodingEnvironment codingEnvironment = codingEnvironmentController.updateCodingEnvironment(
                1L,
                new UpdateCodingEnvironmentInput(
                        "Test Coding Environment 2",
                        "Python Test Coding Environment 2",
                        null
                )
        );
        assertThat(codingEnvironment.getId()).isEqualTo(1L);
    }

    @Test
    public void testBuildCodingEnvironment() {
        CodingEnvironment codingEnvironment = codingEnvironmentController.buildCodingEnvironment(1L);
        assertThat(codingEnvironment.getId()).isEqualTo(1L);
    }

    @Test
    public void testDeleteCodingEnvironment() {
        CodingEnvironment create = codingEnvironmentController.createCodingEnvironment(
                new CreateCodingEnvironmentInput(
                        "Test Coding Environment",
                        "Python Test Coding Environment",
                        "FROM python:3\n"
                )
        );
        CodingEnvironment codingEnvironment = codingEnvironmentController.deleteCodingEnvironment(create.getId());
        assertThat(codingEnvironment.getId()).isEqualTo(create.getId());
    }
}
