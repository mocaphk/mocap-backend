package com.mocaphk.backend.endpoints.dummy.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Author {
    private String id;
    private String firstName;
    private String lastName;
}
