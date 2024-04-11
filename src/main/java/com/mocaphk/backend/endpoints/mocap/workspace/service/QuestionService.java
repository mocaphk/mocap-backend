package com.mocaphk.backend.endpoints.mocap.workspace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question createQuestion(CreateQuestionInput input) {
        Question question = new Question();
        question.setTitle(input.title());
        question.setDescription(input.description());
        question.setLanguage(input.language());
        question.setSampleCode(input.sampleCode());
        question.setCheckingMethod(input.checkingMethod());
        question.setCodingEnvironmentId(input.codingEnvironmentId());
        question.setExecCommand(input.execCommand());
        question.setTimeLimit(input.timeLimit());
        question.setAssignmentId(input.assignmentId());
        question.setTestcases(new ArrayList<>());
        questionRepository.save(question);
        return question;
    }

    public Question updateQuestion(Long id, UpdateQuestionInput input) {
        Question question = getQuestionById(id);
        if (question == null) {
            return null;
        }
        if (StringUtils.isNotBlank(input.title())) {
            question.setTitle(input.title());
        }
        if (StringUtils.isNotBlank(input.description())) {
            question.setDescription(input.description());
        }
        if (input.language() != null) {
            question.setLanguage(input.language());
        }
        if (StringUtils.isNotBlank(input.sampleCode())) {
            question.setSampleCode(input.sampleCode());
        }
        if (input.checkingMethod() != null) {
            question.setCheckingMethod(input.checkingMethod());
        }
        if (input.codingEnvironmentId() != null) {
            question.setCodingEnvironmentId(input.codingEnvironmentId());
        }
        if (StringUtils.isNotBlank(input.execCommand())) {
            question.setExecCommand(input.execCommand());
        }
        if (input.timeLimit() != null) {
            question.setTimeLimit(input.timeLimit());
        }
        if (input.assignmentId() != null) {
            question.setAssignmentId(input.assignmentId());
        }
        questionRepository.save(question);
        return question;
    }

    public Question deleteQuestion(Long id) {
        Question question = getQuestionById(id);
        if (question == null) {
            return null;
        }
        questionRepository.delete(question);
        return question;
    }

    public List<Question> searchPublicQuestions(String code, String keyword) {
        return questionRepository.searchPublicQuestions(code, keyword);
    }

}
