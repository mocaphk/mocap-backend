package com.mocaphk.backend.endpoints.dummy.dto;

public record BookInput(
        String name,
        Integer pageCount,
        AuthorInput author) { }
