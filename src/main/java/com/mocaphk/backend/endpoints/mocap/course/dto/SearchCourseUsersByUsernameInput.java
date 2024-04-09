package com.mocaphk.backend.endpoints.mocap.course.dto;

import org.springframework.lang.NonNull;

public record SearchCourseUsersByUsernameInput(
        @NonNull Long courseId,

        @NonNull String username
) {
}
