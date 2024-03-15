package com.mocaphk.backend.endpoints.mocap.dummy.dto;

public record BookInput(
        String name,
        Integer pageCount,
        AuthorInput author) { }
