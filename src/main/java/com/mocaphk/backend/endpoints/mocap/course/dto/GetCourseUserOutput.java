package com.mocaphk.backend.endpoints.mocap.course.dto;

import com.mocaphk.backend.enums.CourseRole;
import org.springframework.lang.NonNull;

import java.util.List;

public record GetCourseUserOutput(
        @NonNull String id,
        @NonNull String username,
        @NonNull List<CourseRole> roles
        ) {
}
