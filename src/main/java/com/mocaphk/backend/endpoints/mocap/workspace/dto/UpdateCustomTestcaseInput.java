package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import java.util.List;

public record UpdateCustomTestcaseInput (
        List<TestcaseInputEntryInput> input) { }
