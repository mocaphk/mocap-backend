package com.mocaphk.backend.endpoints.mocap.course.dto;

import com.mocaphk.backend.enums.AssignmentType;
import org.springframework.lang.NonNull;

public record CreateAssignmentInput (
        @NonNull Long courseId,

        @NonNull String title,

        @NonNull String description,

        @NonNull AssignmentType type,

        @NonNull String dateDue,

        @NonNull String dateOpen,

        @NonNull String dateClose
) { }