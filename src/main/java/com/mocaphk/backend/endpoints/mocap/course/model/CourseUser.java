package com.mocaphk.backend.endpoints.mocap.course.model;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CourseUser implements Serializable {
    @EmbeddedId
    private CourseUserId id;

    @ManyToOne
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private MocapUser user;

    @OneToMany(mappedBy = "courseUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseUserRole> roles;
}
