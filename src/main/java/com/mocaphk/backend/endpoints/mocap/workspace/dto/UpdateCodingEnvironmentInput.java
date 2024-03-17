package com.mocaphk.backend.endpoints.mocap.workspace.dto;

public record UpdateCodingEnvironmentInput (
        String name,
        String description,
        String dockerfile) { }
