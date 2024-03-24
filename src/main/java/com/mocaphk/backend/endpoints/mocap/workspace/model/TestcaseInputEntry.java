package com.mocaphk.backend.endpoints.mocap.workspace.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestcaseInputEntry {
    private String name;

    private String value;
}
