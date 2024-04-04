package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="coding_environment")
public class CodingEnvironment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String dockerfile;

    private Boolean isBuilt;

    private String dockerImageId;

    private String createdAt;

    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
}
