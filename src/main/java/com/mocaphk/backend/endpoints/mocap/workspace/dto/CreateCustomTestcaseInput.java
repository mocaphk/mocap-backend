package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

import java.util.List;

public record CreateCustomTestcaseInput (
        @NonNull List<TestcaseInputEntryInput> input) { }
