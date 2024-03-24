package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.ProgrammingLanguage;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

public record CreateQuestionInput (
        @NonNull String title,
        String description,
        ProgrammingLanguage language,
        String sampleCode,
        CheckingMethod checkingMethod,
        Long codingEnvironmentId,
        String execCommand,
        @DefaultValue("1000") Integer timeLimit,
        Long assignmentId) { }
