package com.mocaphk.backend.endpoints.mocap.workspace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CustomTestcase;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;
import com.mocaphk.backend.endpoints.mocap.workspace.model.Testcase;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.CustomTestcaseRepository;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.QuestionRepository;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.TestcaseRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestcaseService {
    private final TestcaseRepository testcaseRepository;
    private final CustomTestcaseRepository customTestcaseRepository;
    private final QuestionRepository questionRepository;

    public Testcase getTestcaseById(Long id) {
        return testcaseRepository.findById(id).orElse(null);
    }

    public CustomTestcase getCustomTestcaseById(Long id) {
        return customTestcaseRepository.findById(id).orElse(null);
    }

    public List<Testcase> getTestcasesByQuestionId(Long questionId) {
        return testcaseRepository.findByQuestionId(questionId);
    }

    public List<CustomTestcase> getCustomTestcasesByQuestionId(String userId, Long questionId) {
        return customTestcaseRepository.findByUserIdAndQuestionId(userId, questionId);
    }

    public List<Testcase> createTestcases(Long questionId, List<CreateTestcaseInput> inputs) {
        List<Testcase> testcases = new ArrayList<>();
        for (CreateTestcaseInput input : inputs) {
            Testcase testcase = new Testcase();
            testcase.setInput(input.input());
            testcase.setQuestionId(questionId);
            testcase.setExpectedOutput(input.expectedOutput());
            testcase.setIsHidden(input.isHidden());
            testcases.add(testcase);
        }
        testcaseRepository.saveAll(testcases);
        return testcases;
    }

    public List<CustomTestcase> createCustomTestcases(String userId, Long questionId, List<CreateCustomTestcaseInput> inputs) {
        List<CustomTestcase> customTestcases = new ArrayList<>();
        for (CreateCustomTestcaseInput input : inputs) {
            CustomTestcase customTestcase = new CustomTestcase();
            customTestcase.setUserId(userId);
            customTestcase.setInput(input.input());
            customTestcase.setQuestionId(questionId);
            customTestcases.add(customTestcase);
        }
        customTestcaseRepository.saveAll(customTestcases);
        return customTestcases;
    }

    public Testcase updateTestcase(Long id, UpdateTestcaseInput input) {
        Testcase testcase = getTestcaseById(id);
        if (testcase == null) {
            return null;
        }

        if (!input.input().isEmpty()) {
            testcase.setInput(input.input());
        }
        if (StringUtils.isNotBlank(input.expectedOutput())) {
            testcase.setExpectedOutput(input.expectedOutput());
        }
        if (input.isHidden() != null) {
            testcase.setIsHidden(input.isHidden());
        }
        testcaseRepository.save(testcase);
        return testcase;
    }

    public CustomTestcase updateCustomTestcase(Long id, UpdateCustomTestcaseInput input) {
        CustomTestcase customTestcase = getCustomTestcaseById(id);
        if (customTestcase == null) {
            return null;
        }

        if (!input.input().isEmpty()) {
            customTestcase.setInput(input.input());
        }
        customTestcaseRepository.save(customTestcase);
        return customTestcase;
    }

    public Testcase deleteTestcase(Long id) {
        Testcase testcase = getTestcaseById(id);
        if (testcase == null) {
            return null;
        }
        testcaseRepository.delete(testcase);
        return testcase;
    }

    public CustomTestcase deleteCustomTestcase(Long id) {
        CustomTestcase customTestcase = getCustomTestcaseById(id);
        if (customTestcase == null) {
            return null;
        }
        customTestcaseRepository.delete(customTestcase);
        return customTestcase;
    }
}
