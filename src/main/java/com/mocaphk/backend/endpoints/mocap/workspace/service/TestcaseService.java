package com.mocaphk.backend.endpoints.mocap.workspace.service;

import com.mocaphk.backend.endpoints.mocap.workspace.model.*;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.BaseTestcaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateAndUpdateTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCustomTestcaseInput;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.CustomTestcaseRepository;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.TestcaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestcaseService {
    private final BaseTestcaseRepository baseTestcaseRepository;
    private final TestcaseRepository testcaseRepository;
    private final CustomTestcaseRepository customTestcaseRepository;

    public BaseTestcase getBaseTestcaseById(Long id) {
        return baseTestcaseRepository.findById(id).orElse(null);
    }

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

    public List<Testcase> createAndUpdateTestcases(Long questionId, List<CreateAndUpdateTestcaseInput> inputs) {
        List<Testcase> oldTestcases = getTestcasesByQuestionId(questionId);
        List<Testcase> testcases = new ArrayList<>();
        for (CreateAndUpdateTestcaseInput input : inputs) {
            Testcase testcase;
            if (input.id() == null) {
                testcase = new Testcase();
                testcase.setInput(input.input().stream()
                        .map(i -> new TestcaseInputEntry(i.name(), i.value()))
                        .collect(Collectors.toList()));
                testcase.setQuestionId(questionId);
                testcase.setExpectedOutput(input.expectedOutput());
                testcase.setIsHidden(input.isHidden());
            } else {
                testcase = getTestcaseById(input.id());
                if (testcase == null || !testcase.getQuestionId().equals(questionId)) {
                    continue;
                }
                oldTestcases.remove(testcase);
                if (!input.input().isEmpty()) {
                    testcase.setInput(input.input().stream()
                            .map(i -> new TestcaseInputEntry(i.name(), i.value()))
                            .collect(Collectors.toList()));
                }
                if (StringUtils.isNotBlank(input.expectedOutput())) {
                    testcase.setExpectedOutput(input.expectedOutput());
                }
                if (input.isHidden() != null) {
                    testcase.setIsHidden(input.isHidden());
                }
            }
            testcases.add(testcase);
        }
        testcaseRepository.deleteAll(oldTestcases);
        testcaseRepository.saveAll(testcases);
        return testcases;
    }

    public List<CustomTestcase> createCustomTestcases(String userId, Long questionId, List<CreateCustomTestcaseInput> inputs) {
        List<CustomTestcase> customTestcases = new ArrayList<>();
        for (CreateCustomTestcaseInput input : inputs) {
            CustomTestcase customTestcase = new CustomTestcase();
            customTestcase.setUserId(userId);
            customTestcase.setInput(input.input().stream()
                    .map(i -> new TestcaseInputEntry(i.name(), i.value()))
                    .collect(Collectors.toList()));
            customTestcase.setQuestionId(questionId);
            customTestcases.add(customTestcase);
        }
        customTestcaseRepository.saveAll(customTestcases);
        return customTestcases;
    }

    public CustomTestcase updateCustomTestcase(Long id, UpdateCustomTestcaseInput input) {
        CustomTestcase customTestcase = getCustomTestcaseById(id);
        if (customTestcase == null) {
            return null;
        }

        if (!input.input().isEmpty()) {
            customTestcase.setInput(input.input().stream()
                    .map(i -> new TestcaseInputEntry(i.name(), i.value()))
                    .collect(Collectors.toList()));
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
