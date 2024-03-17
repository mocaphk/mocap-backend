package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.endpoints.mocap.course.model.Assignment;
import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.ProgrammingLanguage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    private String sampleCode;

    @Enumerated(EnumType.STRING)
    private CheckingMethod checkingMethod;

    @Column(name = "coding_environment_id")
    private Long codingEnvironmentId;

    @ManyToOne
    @JoinColumn(name = "coding_environment_id", insertable = false, updatable = false)
    private CodingEnvironment codingEnvironment;

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
}
