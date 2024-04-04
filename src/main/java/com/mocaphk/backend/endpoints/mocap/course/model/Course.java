package com.mocaphk.backend.endpoints.mocap.course.model;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CodingEnvironment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;

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

    private String year;

    private String createdAt;

    private String updatedAt;

    private String barColor;

    @Transient
    private List<MocapUser> admins;

    @Transient
    private List<MocapUser> lecturers;

    @Transient
    private List<MocapUser> tutors;

    @Transient
    private List<MocapUser> students;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExternalLink> externalLinks;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodingEnvironment> codingEnvironments;
}
