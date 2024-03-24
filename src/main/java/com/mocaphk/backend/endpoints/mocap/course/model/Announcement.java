package com.mocaphk.backend.endpoints.mocap.course.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String createdAt;

    private String updatedAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
