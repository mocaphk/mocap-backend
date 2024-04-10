package com.mocaphk.backend.endpoints.mocap.user.dto;

import org.springframework.lang.NonNull;

import java.util.List;

public record GetUserOutput (
        @NonNull String id,
        @NonNull String username
) {
}
