package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

import java.util.List;

public record CreateAndUpdateTestcaseInput(
        Long id,
        @NonNull List<TestcaseInputEntryInput> input,
        String expectedOutput,
        Boolean isHidden) { }
