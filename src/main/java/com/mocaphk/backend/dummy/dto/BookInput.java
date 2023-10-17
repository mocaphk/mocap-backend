package com.mocaphk.backend.dummy.dto;

public record BookInput(
        String name,
        Integer pageCount,
        AuthorInput author) { }
