package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.endpoints.mocap.course.model.Assignment;
import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.ProgrammingLanguage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    @Column(columnDefinition = "TEXT")
    private String sampleCode;

    @Enumerated(EnumType.STRING)
    private CheckingMethod checkingMethod;

    @Column(name = "coding_environment_id")
    private Long codingEnvironmentId;

    @ManyToOne
    @JoinColumn(name = "coding_environment_id", insertable = false, updatable = false)
    private CodingEnvironment codingEnvironment;

    @Column(columnDefinition = "TEXT")
    private String execCommand;

    private Integer timeLimit;

//    private Integer memoryLimit;

    @Column(name = "assignment_id")
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Testcase> testcases;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomTestcase> customTestcases;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attempt> attempts;

    @Column(columnDefinition = "boolean default false")
    private Boolean isPublic;
}
