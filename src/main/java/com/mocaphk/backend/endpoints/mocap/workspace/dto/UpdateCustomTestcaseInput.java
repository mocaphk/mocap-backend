package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import java.util.List;

import com.mocaphk.backend.endpoints.mocap.workspace.model.TestcaseInputEntry;

public record UpdateCustomTestcaseInput (
        List<TestcaseInputEntry> input) { }
