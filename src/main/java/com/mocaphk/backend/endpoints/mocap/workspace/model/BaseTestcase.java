package com.mocaphk.backend.endpoints.mocap.workspace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class BaseTestcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<TestcaseInputEntry> input;

    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    // TODO: temporary solution, need to find a better way to handle this
    public Boolean getIsHidden() {
        return false;
    }

    /**
     * Check whether the result of the code execution with this testcase is correct,
     * will set the result's isCorrect field to true if the result is correct, or otherwise.
     *
     * @param result the result of the code execution with this testcase
     * @param sampleResult the result of the code execution with the sample code and this testcase
     * @return true if the result is correct, false otherwise
     */
    public abstract boolean check(CodeExecutionResult result, CodeExecutionResult sampleResult);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseTestcase o) {
            return Objects.equals(o.id, this.id);
        }
        return false;
    }
}
