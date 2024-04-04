package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

public record CreateCodingEnvironmentInput (
        @NonNull String name,
        String description,
        @NonNull String dockerfile,
        Long courseId) { }
