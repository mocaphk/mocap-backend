package com.mocaphk.backend.endpoints.mocap.workspace.controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CodingEnvironment;
import com.mocaphk.backend.endpoints.mocap.workspace.service.CodingEnvironmentService;
import com.mocaphk.backend.enums.Roles;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CodingEnvironmentController {
    private final CodingEnvironmentService codingEnvironmentService;

    @QueryMapping(name = "codingEnvironment")
    public CodingEnvironment getCodingEnvironmentById(@Argument Long id) {
        log.debug("getCodingEnvironmentById: {}", id);
        return codingEnvironmentService.getCodingEnvironmentById(id);
    }

    @MutationMapping(name = "createCodingEnvironment")
    public CodingEnvironment createCodingEnvironment(@Argument CreateCodingEnvironmentInput codingEnvironmentInput) {
        log.debug("createCodingEnvironment: {}", codingEnvironmentInput);
        return codingEnvironmentService.createCodingEnvironment(codingEnvironmentInput);
    }

    @MutationMapping(name = "updateCodingEnvironment")
    public CodingEnvironment updateCodingEnvironment(@Argument Long id, @Argument UpdateCodingEnvironmentInput codingEnvironmentInput) {
        log.debug("updateCodingEnvironment: {}, {}", id, codingEnvironmentInput);
        return codingEnvironmentService.updateCodingEnvironment(id, codingEnvironmentInput);
    }

    @MutationMapping(name = "deleteCodingEnvironment")
    public CodingEnvironment deleteCodingEnvironment(@Argument Long id) {
        log.debug("deleteCodingEnvironment: {}", id);
        return codingEnvironmentService.deleteCodingEnvironment(id);
    }

    @MutationMapping(name = "buildCodingEnvironment")
    public CodingEnvironment buildCodingEnvironment(@Argument Long id) {
        log.debug("buildCodingEnvironment: {}", id);
        return codingEnvironmentService.buildCodingEnvironment(id);
    }
}
