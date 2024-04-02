package com.mocaphk.backend.endpoints.mocap.course.dto;

import org.springframework.lang.NonNull;

public record CreateExternalLinkInput(
        @NonNull Long courseId,

        @NonNull String title,

        @NonNull String description,

        @NonNull String link
        ) { }