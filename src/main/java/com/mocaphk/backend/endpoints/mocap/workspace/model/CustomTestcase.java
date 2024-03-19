package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.enums.CheckingMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.mocaphk.backend.endpoints.mocap.workspace.model.CodeExecutionResult.combineOutput;

import com.mocaphk.backend.endpoints.mocap.user.model.User;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CustomTestcase extends BaseTestcase {
    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Override
    public boolean check(CodeExecutionResult result, CodeExecutionResult sampleResult) {
        result.setTestcaseId(getId());
        sampleResult.setTestcaseId(getId());
        if (getQuestion().getCheckingMethod() == CheckingMethod.CONSOLE) {
            String output = combineOutput(result.getOutput());
            if (sampleResult != null && combineOutput(sampleResult.getOutput()).equals(output)) {
                result.setIsCorrect(true);
                return true;
            }
        } else if (getQuestion().getCheckingMethod() == CheckingMethod.FUNCTION) {
            // TODO: implement function checking
        }
        result.setIsCorrect(false);
        return false;
    }
}
