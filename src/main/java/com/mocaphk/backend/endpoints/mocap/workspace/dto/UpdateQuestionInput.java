package com.mocaphk.backend.endpoints.mocap.workspace.dto;

import com.mocaphk.backend.enums.CheckingMethod;
import com.mocaphk.backend.enums.ProgrammingLanguage;

public record UpdateQuestionInput (
        String title,
        String description,
        ProgrammingLanguage language,
        String sampleCode,
        CheckingMethod checkingMethod,
        Long codingEnvironmentId,
        String execCommand,
        Integer timeLimit,
        Long assignmentId) { }
