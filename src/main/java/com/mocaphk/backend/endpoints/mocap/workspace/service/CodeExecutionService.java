package com.mocaphk.backend.endpoints.mocap.workspace.service;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.mocaphk.backend.components.DockerManager;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.TestcaseInputEntryInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.*;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.AttemptResultRepository;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.CodeExecutionResultRepository;
import com.mocaphk.backend.enums.CodeStream;
import com.mocaphk.backend.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.mocaphk.backend.enums.CheckingMethod;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CodeExecutionService {
    private static final int MAX_START_EXECUTION_TIME = 1000;

    private final AttemptResultRepository attemptResultRepository;
    private final CodeExecutionResultRepository codeExecutionResultRepository;
    private final AttemptService attemptService;
    private final QuestionService questionService;
    private final DockerManager dockerManager;

    public AttemptResult runAttempt(Long attemptId, List<List<TestcaseInputEntryInput>> testcaseInputs) throws IOException {
        Attempt attempt = attemptService.getAttemptById(attemptId);
        Question question = attempt.getQuestion();
        CodingEnvironment codingEnvironment = question.getCodingEnvironment();
        if (codingEnvironment == null || !codingEnvironment.getIsBuilt()) {
            log.error("Coding environment not found or not built");
            return null;
        }

        AttemptResult attemptResult = new AttemptResult();
        attemptResult.setAttempt(attempt);
        attemptResult.setCreatedAt(DateUtils.now());

        List<CodeExecutionResult> results = new ArrayList<>();

        List<BaseTestcase> allTestcases = new ArrayList<>();
        if (testcaseInputs == null || testcaseInputs.isEmpty()) {
            CustomTestcase defaultTestcase = new CustomTestcase();
            defaultTestcase.setQuestion(question);
            allTestcases.add(defaultTestcase);
        } else {
            for (var input : testcaseInputs) {
                CustomTestcase customTestcase = new CustomTestcase();
                customTestcase.setQuestion(question);
                customTestcase.setInput(input.stream().map(i -> new TestcaseInputEntry(i.name(), i.value())).toList());
                allTestcases.add(customTestcase);
            }
        }

        String studentCode = attempt.getCode();
        if (studentCode == null) {
            studentCode = "";
        }
        String sampleCode = question.getSampleCode();
        if (sampleCode == null) {
            sampleCode = "";
        }

        String studentContainerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(studentContainerId);
        String studentFileName = dockerManager.copyFileToContainer(studentContainerId, studentCode, "/");

        String sampleContainerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(sampleContainerId);
        String sampleFileName = dockerManager.copyFileToContainer(sampleContainerId, sampleCode, "/");

        try {
            for (var testcase : allTestcases) {
                CodeExecutionResult result = execute(question, studentContainerId, studentFileName, testcase);
                attempt.setExecutedAt(DateUtils.now());
                CodeExecutionResult sampleResult = execute(question, sampleContainerId, sampleFileName, testcase);
                testcase.check(result, sampleResult);
                result.setSampleOutput(sampleResult.getOutput());
                result.setAttemptResultId(attemptResult.getId());
                results.add(result);
            }
        } finally {
            dockerManager.stopContainer(studentContainerId);
            dockerManager.removeContainer(studentContainerId, true);
            dockerManager.stopContainer(sampleContainerId);
            dockerManager.removeContainer(sampleContainerId, true);
        }
        attemptResult.setResults(results);

        return attemptResult;
    }

    public CodeExecutionResult runTestcase(Long attemptId, List<TestcaseInputEntryInput> testcaseInput) throws IOException {
        Attempt attempt = attemptService.getAttemptById(attemptId);
        Question question = attempt.getQuestion();
        CodingEnvironment codingEnvironment = question.getCodingEnvironment();
        if (codingEnvironment == null || !codingEnvironment.getIsBuilt()) {
            log.error("Coding environment not found or not built");
            return null;
        }

        if (testcaseInput == null) {
            log.error("Testcase is null");
            return null;
        }
        CustomTestcase testcase = new CustomTestcase();
        testcase.setQuestion(question);
        testcase.setInput(testcaseInput.stream().map(i -> new TestcaseInputEntry(i.name(), i.value())).toList());

        CodeExecutionResult result;
        CodeExecutionResult sampleResult;

        String studentCode = attempt.getCode();
        if (studentCode == null) {
            studentCode = "";
        }
        String sampleCode = question.getSampleCode();
        if (sampleCode == null) {
            sampleCode = "";
        }

        String studentContainerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(studentContainerId);
        String studentFileName = dockerManager.copyFileToContainer(studentContainerId, studentCode, "/");

        String sampleContainerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(sampleContainerId);
        String sampleFileName = dockerManager.copyFileToContainer(sampleContainerId, sampleCode, "/");

        try {
            result = execute(question, studentContainerId, studentFileName, testcase);
            sampleResult = execute(question, sampleContainerId, sampleFileName, testcase);
        } finally {
            dockerManager.stopContainer(studentContainerId);
            dockerManager.removeContainer(studentContainerId, true);
            dockerManager.stopContainer(sampleContainerId);
            dockerManager.removeContainer(sampleContainerId, true);
        }
        testcase.check(result, sampleResult);
        result.setSampleOutput(sampleResult.getOutput());
        return result;
    }

    public CodeExecutionResult runTestcaseWithCode(Long questionId, List<TestcaseInputEntryInput> testcaseInput, String code) throws IOException {
        Question question = questionService.getQuestionById(questionId);
        CodingEnvironment codingEnvironment = question.getCodingEnvironment();
        if (codingEnvironment == null || !codingEnvironment.getIsBuilt()) {
            log.error("Coding environment not found or not built");
            return null;
        }

        if (testcaseInput == null) {
            log.error("Testcase is null");
            return null;
        }
        CustomTestcase testcase = new CustomTestcase();
        testcase.setQuestion(question);
        testcase.setInput(testcaseInput.stream().map(i -> new TestcaseInputEntry(i.name(), i.value())).toList());

        CodeExecutionResult result;

        if (code == null) {
            code = "";
        }

        String containerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(containerId);
        String fileName = dockerManager.copyFileToContainer(containerId, code, "/");

        try {
            result = execute(question, containerId, fileName, testcase);
        } finally {
            dockerManager.stopContainer(containerId);
            dockerManager.removeContainer(containerId, true);
        }
        result.setSampleOutput(result.getOutput());
        return result;
    }

    public AttemptResult runTestcasesWithCode(Long questionId, List<List<TestcaseInputEntryInput>> testcaseInputs, String code) throws IOException {
        Question question = questionService.getQuestionById(questionId);
        CodingEnvironment codingEnvironment = question.getCodingEnvironment();
        if (codingEnvironment == null || !codingEnvironment.getIsBuilt()) {
            log.error("Coding environment not found or not built");
            return null;
        }

        List<BaseTestcase> allTestcases = new ArrayList<>();
        if (testcaseInputs == null || testcaseInputs.isEmpty()) {
            CustomTestcase defaultTestcase = new CustomTestcase();
            defaultTestcase.setQuestion(question);
            allTestcases.add(defaultTestcase);
        } else {
            for (var input : testcaseInputs) {
                CustomTestcase customTestcase = new CustomTestcase();
                customTestcase.setQuestion(question);
                customTestcase.setInput(input.stream().map(i -> new TestcaseInputEntry(i.name(), i.value())).toList());
                allTestcases.add(customTestcase);
            }
        }

        AttemptResult attemptResult = new AttemptResult();
        attemptResult.setCreatedAt(DateUtils.now());

        List<CodeExecutionResult> results = new ArrayList<>();

        if (code == null) {
            code = "";
        }

        String containerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(containerId);
        String fileName = dockerManager.copyFileToContainer(containerId, code, "/");

        try {
            for (var testcase : allTestcases) {
                CodeExecutionResult result = execute(question, containerId, fileName, testcase);
                result.setSampleOutput(result.getOutput());
                results.add(result);
            }
        } finally {
            dockerManager.stopContainer(containerId);
            dockerManager.removeContainer(containerId, true);
        }
        attemptResult.setResults(results);

        return attemptResult;
    }

    public AttemptResult submitAttempt(Long attemptId) throws IOException {
        Attempt attempt = attemptService.getAttemptById(attemptId);
        Question question = attempt.getQuestion();
        List<Testcase> testcases = question.getTestcases();
        CodingEnvironment codingEnvironment = question.getCodingEnvironment();
        if (codingEnvironment == null || !codingEnvironment.getIsBuilt()) {
            log.error("Coding environment not found or not built");
            return null;
        }

        AttemptResult attemptResult = new AttemptResult();
        attemptResult.setAttempt(attempt);
        attemptResult.setCreatedAt(DateUtils.now());
        attemptResult = attemptResultRepository.save(attemptResult);
        attempt.setResult(attemptResult);

        List<BaseTestcase> allTestcases = new ArrayList<>(testcases);
        if (allTestcases.isEmpty()) {
            CustomTestcase defaultTestcase = new CustomTestcase();
            defaultTestcase.setQuestion(question);
            allTestcases.add(defaultTestcase);
        }

        List<CodeExecutionResult> results = new ArrayList<>();

        String studentCode = attempt.getCode();
        if (studentCode == null) {
            studentCode = "";
        }
        String sampleCode = question.getSampleCode();
        if (sampleCode == null) {
            sampleCode = "";
        }

        String studentContainerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(studentContainerId);
        String studentFileName = dockerManager.copyFileToContainer(studentContainerId, studentCode, "/");

        String sampleContainerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(sampleContainerId);
        String sampleFileName = dockerManager.copyFileToContainer(sampleContainerId, sampleCode, "/");

        try {
            for (var testcase : allTestcases) {
                CodeExecutionResult result = execute(question, studentContainerId, studentFileName, testcase);
                attempt.setExecutedAt(DateUtils.now());
                CodeExecutionResult sampleResult = execute(question, sampleContainerId, sampleFileName, testcase);
                testcase.check(result, sampleResult);
                result.setSampleOutput(sampleResult.getOutput());
                result.setAttemptResultId(attemptResult.getId());

                codeExecutionResultRepository.save(result);

                // hide the testcase input if it is hidden
                if (testcase.getIsHidden()) {
                    List<TestcaseInputEntry> emptyInputList = new ArrayList<>();
                    List<CodeExecutionResult.CodeExecutionOutput> emptyResultList = new ArrayList<>();
                    result.setInput(emptyInputList);
                    result.setOutput(emptyResultList);
                    result.setSampleOutput(emptyResultList);
                }

                results.add(result);
            }
        } finally {
            dockerManager.stopContainer(studentContainerId);
            dockerManager.removeContainer(studentContainerId, true);
            dockerManager.stopContainer(sampleContainerId);
            dockerManager.removeContainer(sampleContainerId, true);
        }
        attempt.setIsSubmitted(true);
        attemptResult.setResults(results);

        return attemptResult;
    }

    /**
     * Execute code without checking the result
     *
     * @param question The question that the code is for
     * @param containerId The container ID to execute the code
     * @param fileName The file name of the code
     * @param testcase The testcase to be executed
     * @return The result of the execution
     * @throws IOException
     */
    public CodeExecutionResult execute(
            Question question,
            String containerId,
            String fileName,
            BaseTestcase testcase
    ) throws IOException {
        CodeExecutionResult result = new CodeExecutionResult();
        result.setInput(new ArrayList<>());
        if (testcase != null && testcase.getInput() != null) {
            for (TestcaseInputEntry input : testcase.getInput()) {
                result.getInput().add(new TestcaseInputEntry(input.getName(), input.getValue()));
            }
            result.setIsHidden(testcase.getIsHidden());
        } else {
            result.setIsHidden(false);
        }
        result.setIsExecutionSuccess(true);
        result.setIsExceedTimeLimit(true);

        List<CodeExecutionResult.CodeExecutionOutput> output = new ArrayList<>();

        PipedOutputStream stdinWrite = null;
        PipedInputStream stdinRead = null;
        if (question.getCheckingMethod() == CheckingMethod.CONSOLE) {
            stdinWrite = new PipedOutputStream();
            stdinRead = new PipedInputStream(stdinWrite);
        }

        // TODO: support dynamic time stdin, idea: create a cache variable map from attempt id to stdin and wait for input
        var resultCallback = dockerManager.execCommand(
                containerId,
                stdinRead,
                new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(Frame item) {
                        output.add(
                                new CodeExecutionResult.CodeExecutionOutput(
                                        new String(item.getPayload()),
                                        CodeStream.valueOf(item.getStreamType().name())
                                )
                        );
                        log.info(item.toString());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        result.setIsExecutionSuccess(false);
                        super.onError(throwable);
                    }
                },
                "/bin/sh", "-c", question.getExecCommand().replace("{mainFile}", "/" + fileName)
        );

        try {
            if (!resultCallback.awaitStarted(MAX_START_EXECUTION_TIME, TimeUnit.MILLISECONDS)) {
                log.error("Could not start execution within time limit");
                result.setOutput(output);
                return result;
            }

            if (testcase.getInput() != null && question.getCheckingMethod() == CheckingMethod.CONSOLE) {
                for (TestcaseInputEntry input : testcase.getInput()) {
                    stdinWrite.write(input.getValue().getBytes());
                    stdinWrite.write("\n".getBytes());
                }
                stdinWrite.close();
            }

            if (!resultCallback.awaitCompletion(question.getTimeLimit(), TimeUnit.MILLISECONDS)) {
                result.setOutput(output);
                return result;
            }
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        result.setOutput(output);
        result.setIsExceedTimeLimit(false);

        return result;
    }
}
