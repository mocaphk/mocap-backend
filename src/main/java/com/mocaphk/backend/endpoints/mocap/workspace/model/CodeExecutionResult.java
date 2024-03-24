package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.enums.CodeStream;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CodeExecutionResult {
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class CodeExecutionOutput {
        private String payload;

        @Enumerated(EnumType.STRING)
        private CodeStream streamType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_result_id")
    private Long attemptResultId;

    @ManyToOne
    @JoinColumn(name = "attempt_result_id", insertable = false, updatable = false)
    private AttemptResult attemptResult;

    @Column(name = "testcase_id")
    private Long testcaseId;

    @ManyToOne
    @JoinColumn(name = "testcase_id", insertable = false, updatable = false)
    private BaseTestcase testcase;

    @ElementCollection
    private List<CodeExecutionOutput> output;

    private Boolean isExecutionSuccess;

    private Boolean isCorrect;

    private Boolean isExceedTimeLimit;

    public static String combineOutput(List<CodeExecutionOutput> output, CodeStream streamType) {
        return output.stream()
                .filter(o -> o.getStreamType() == streamType)
                .map(CodeExecutionOutput::getPayload)
                .reduce("", String::concat);
    }

    public static String combineOutput(List<CodeExecutionOutput> output) {
        return combineOutput(output, CodeStream.STDOUT);
    }
}
