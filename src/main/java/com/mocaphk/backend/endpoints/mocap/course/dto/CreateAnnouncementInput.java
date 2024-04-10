package com.mocaphk.backend.endpoints.mocap.course.dto;

import org.springframework.lang.NonNull;

public record CreateAnnouncementInput(
        @NonNull String title,
        @NonNull String content,
        @NonNull Long courseId
        ) { }
