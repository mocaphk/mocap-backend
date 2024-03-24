package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

import java.util.List;

public record CreateTestcaseInput (
        @NonNull List<TestcaseInputEntryInput> input,
        String expectedOutput,
        @NonNull Boolean isHidden) { }
