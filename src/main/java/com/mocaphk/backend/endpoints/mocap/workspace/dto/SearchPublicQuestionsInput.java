package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import org.springframework.lang.NonNull;

public record SearchPublicQuestionsInput(
        @NonNull String courseCode,

        @NonNull String keyword
) {
}
