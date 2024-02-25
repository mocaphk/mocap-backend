package com.mocaphk.backend.endpoints.keycloak.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user_entity")
public class User {
    @Id
    private String id;

    @Column(nullable = true)
    private String email;

    private Boolean emailVerified;

    private Boolean enabled;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    private String username;

    @Column(name = "created_timestamp")
    private Long createdAt;
}
