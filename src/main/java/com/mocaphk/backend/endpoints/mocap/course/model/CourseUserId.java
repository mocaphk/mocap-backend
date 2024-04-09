package com.mocaphk.backend.endpoints.mocap.course.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CourseUserId implements Serializable {

    @Column(name = "courseId")
    public Long courseId;

    @Column(name = "userId")
    public String userId;
}