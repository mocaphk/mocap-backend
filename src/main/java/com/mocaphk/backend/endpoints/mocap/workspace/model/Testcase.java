package com.mocaphk.backend.endpoints.mocap.workspace.model;

import static com.mocaphk.backend.endpoints.mocap.workspace.model.CodeExecutionResult.combineOutput;

import com.mocaphk.backend.enums.CheckingMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Testcase extends BaseTestcase {
    private String expectedOutput;

    private Boolean isHidden;

    @Override
    public boolean check(CodeExecutionResult result, CodeExecutionResult sampleResult) {
        if (getQuestion().getCheckingMethod() == CheckingMethod.CONSOLE) {
            String output = combineOutput(result.getOutput());
            if (output.equals(getExpectedOutput())) {
                result.setIsCorrect(true);
                return true;
            }
            if (sampleResult != null && combineOutput(sampleResult.getOutput()).equals(output)) {
                result.setIsCorrect(true);
                return true;
            }
        } else if (getQuestion().getCheckingMethod() == CheckingMethod.FUNCTION) {
            // TODO: implement function checking
        }
        return false;
    }
}
