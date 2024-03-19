package com.mocaphk.backend.endpoints.mocap.workspace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class AttemptResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "result")
    private Attempt attempt;

    @OneToMany(mappedBy = "attemptResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeExecutionResult> results;

    private String createdAt;
}
