package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import java.util.List;

public record UpdateTestcaseInput (
        List<TestcaseInputEntryInput> input,
        String expectedOutput,
        Boolean isHidden) { }
