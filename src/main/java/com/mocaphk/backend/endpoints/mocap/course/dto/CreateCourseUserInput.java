package com.mocaphk.backend.endpoints.mocap.course.dto;

import com.mocaphk.backend.enums.CourseRole;
import org.springframework.lang.NonNull;

import java.util.List;

public record CreateCourseUserInput(
        @NonNull Long courseId,

        @NonNull String username,

        @NonNull List<CourseRole> roles
) {
}
