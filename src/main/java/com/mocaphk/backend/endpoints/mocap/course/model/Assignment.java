package com.mocaphk.backend.endpoints.mocap.course.model;

import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.enums.AssignmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private AssignmentType type;

    private String dateDue;

    private String dateOpen;

    private String dateClose;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}
