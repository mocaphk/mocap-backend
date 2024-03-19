package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

public record TestcaseInputEntryInput (
        @NonNull String name,
        @NonNull String value) { }
