package com.mocaphk.backend.endpoints.mocap.course.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CourseUserId implements Serializable {

    @Column(name = "courseId")
    public Long courseId;

    @Column(name = "userId")
    public String userId;
}