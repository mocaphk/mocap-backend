package com.mocaphk.backend.endpoints.mocap.course.model;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.utils.DateUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String createdAt;

    private String updatedAt;

    @Column(name = "created_by_id")
    private String createdById;

    @ManyToOne
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private MocapUser createdBy;

    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @ManyToMany
    private Set<MocapUser> readBy;

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = DateUtils.now();
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = DateUtils.now();
    }
}
