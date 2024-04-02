package com.mocaphk.backend.endpoints.mocap.user.model;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
public class MocapUser {
    // This id should match the id of the user in the keycloak database
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

    public MocapUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.emailVerified = user.getEmailVerified();
        this.enabled = user.getEnabled();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.createdAt = user.getCreatedAt();
    }
}
