package com.mocaphk.backend.endpoints.mocap.user.model;

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

    @ManyToMany
    private List<Course> courses;
}
