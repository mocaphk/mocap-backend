package com.mocaphk.backend.endpoints.mocap.course.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.mocaphk.backend.endpoints.mocap.user.model.User;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String description;

    private String createdAt;

    // TODO: schema map to instructors and students, and coding env
    @ManyToMany(mappedBy = "courses")
    private List<User> users;

    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "course")
    private List<Announcement> announcements;
}
