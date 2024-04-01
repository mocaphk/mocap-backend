package com.mocaphk.backend.endpoints.mocap.course.model;

import com.mocaphk.backend.enums.CourseRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "course_user_role")
@NoArgsConstructor
@Getter
@Setter
public class CourseUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "courseId", referencedColumnName = "courseId"),
            @JoinColumn(name = "userId", referencedColumnName = "userId")
    })
    private CourseUser courseUser;

    @Enumerated(EnumType.STRING)
    private CourseRole role;
}