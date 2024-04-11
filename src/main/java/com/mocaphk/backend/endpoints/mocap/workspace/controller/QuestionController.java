package com.mocaphk.backend.endpoints.mocap.workspace.controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.SearchPublicQuestionsInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateQuestionInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.endpoints.mocap.workspace.service.QuestionService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @QueryMapping(name = "question")
    public Question getQuestionById(@Argument Long id) {
        log.debug("getQuestionById: {}", id);
        return questionService.getQuestionById(id);
    }

    @QueryMapping(name = "searchPublicQuestions")
    public List<Question> searchPublicQuestions(@Argument SearchPublicQuestionsInput searchPublicQuestionsInput) {
        log.debug("searchPublicQuestions: {}", searchPublicQuestionsInput);
        return questionService.searchPublicQuestions(searchPublicQuestionsInput.courseCode(), searchPublicQuestionsInput.keyword());
    }

    @MutationMapping(name = "createQuestion")
    public Question createQuestion(@Argument CreateQuestionInput questionInput) {
        log.debug("createQuestion: {}", questionInput);
        return questionService.createQuestion(questionInput);
    }

    @MutationMapping(name = "updateQuestion")
    public Question updateQuestion(@Argument Long id, @Argument UpdateQuestionInput questionInput) {
        log.debug("updateQuestion: {}, {}", id, questionInput);
        return questionService.updateQuestion(id, questionInput);
    }

    @MutationMapping(name = "deleteQuestion")
    public Question deleteQuestion(@Argument Long id) {
        log.debug("deleteQuestion: {}", id);
        return questionService.deleteQuestion(id);
    }
}
