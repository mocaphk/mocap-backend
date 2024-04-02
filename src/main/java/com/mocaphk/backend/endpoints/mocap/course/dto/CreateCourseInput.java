package com.mocaphk.backend.endpoints.mocap.course.dto;

import org.springframework.lang.NonNull;

public record CreateCourseInput (
        @NonNull String code,

        @NonNull String name,

        @NonNull String description,

        @NonNull String barColor
        ) { }