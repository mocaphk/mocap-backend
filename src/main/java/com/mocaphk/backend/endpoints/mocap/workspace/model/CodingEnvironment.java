package com.mocaphk.backend.endpoints.mocap.workspace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
//@Table(schema="test", name="coding_environment")
public class CodingEnvironment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String dockerfile;

    private Boolean isBuilt;

    private String dockerImageId;
}
