package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

import com.mocaphk.backend.endpoints.mocap.workspace.model.TestcaseInputEntry;

import java.util.List;

public record CreateCustomTestcaseInput (
        @NonNull List<TestcaseInputEntry> input) { }
