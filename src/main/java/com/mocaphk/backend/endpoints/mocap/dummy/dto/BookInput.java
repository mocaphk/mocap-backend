package com.mocaphk.backend.endpoints.mocap.dummy.dto;

import com.mocaphk.backend.endpoints.mocap.dummy.enums.BookType;

public record BookInput(
        String name,
        Integer pageCount,
        BookType type,
        AuthorInput author) { }
