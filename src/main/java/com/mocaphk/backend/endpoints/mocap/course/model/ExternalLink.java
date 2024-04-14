package com.mocaphk.backend.endpoints.mocap.course.model;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ExternalLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String link;

    private String createdAt;

    private String updatedAt;

    @ManyToOne
    private MocapUser createdBy;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
