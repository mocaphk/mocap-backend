package com.mocaphk.backend.endpoints.mocap.workspace.service;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.mocaphk.backend.components.DockerManager;
import com.mocaphk.backend.endpoints.mocap.workspace.model.*;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.AttemptResultRepository;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.CodeExecutionResultRepository;
import com.mocaphk.backend.enums.CodeStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.mocaphk.backend.enums.CheckingMethod;
@Service
@Slf4j
@RequiredArgsConstructor
public class CodeExecutionService {
    private static final int MAX_START_EXECUTION_TIME = 1000;

    private final AttemptResultRepository attemptResultRepository;
    private final CodeExecutionResultRepository codeExecutionResultRepository;
    private final AttemptService attemptService;
    private final TestcaseService testcaseService;
    private final DockerManager dockerManager;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // TODO: store result, add controller

    public AttemptResult runAttempt(String userId, Long attemptId) throws IOException {
        Attempt attempt = attemptService.getAttemptById(attemptId);
        Question question = attempt.getQuestion();
        List<Testcase> testcases = question.getTestcases();
        List<CustomTestcase> customTestcases = testcaseService.getCustomTestcasesByQuestionId(userId, question.getId());
        CodingEnvironment codingEnvironment = question.getCodingEnvironment();
        if (codingEnvironment == null || !codingEnvironment.getIsBuilt()) {
            log.error("Coding environment not found or not built");
            return null;
        }

        AttemptResult attemptResult = new AttemptResult();
        attemptResult.setAttempt(attempt);
        attemptResult.setResults(new ArrayList<>());
        attemptResult.setCreatedAt(dateTimeFormatter.format(LocalDateTime.now()));
//        attemptResult = attemptResultRepository.save(attemptResult);

        for (var testcase : Stream.concat(testcases.stream(), customTestcases.stream()).toList()) {
            CodeExecutionResult result = execute(attempt.getCode(), question, codingEnvironment, testcase);
            CodeExecutionResult sampleResult = execute(question.getSampleCode(), question, codingEnvironment, testcase);
            testcase.check(result, sampleResult);
            result.setAttemptResultId(attemptResult.getId());
            attemptResult.getResults().add(result);
//            codeExecutionResultRepository.save(result);
        }

        return attemptResult;
    }

    /**
     * Execute code without checking the result
     *
     * @param code The code to be executed
     * @param question The question that the code is for
     * @param codingEnvironment The coding environment to execute the code
     * @param testcase The testcase to be executed
     * @return The result of the execution
     * @throws IOException
     */
    public CodeExecutionResult execute(
            String code,
            Question question,
            CodingEnvironment codingEnvironment,
            BaseTestcase testcase
    ) throws IOException {
        String containerId = dockerManager.createContainer(codingEnvironment.getDockerImageId());
        dockerManager.startContainer(containerId);
        String fileName = dockerManager.copyFileToContainer(containerId, code, "/");

        CodeExecutionResult result = new CodeExecutionResult();
        result.setTestcaseId(testcase.getId());
        result.setOutput(new ArrayList<>());
        result.setIsExecutionSuccess(true);
        result.setIsExceedTimeLimit(true);

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
                        result.getOutput().add(
                                new CodeExecutionResult.CodeExecutionOutput(
                                        new String(item.getPayload()),
                                        CodeStream.valueOf(item.getStreamType().name())
                                )
                        );
                        log.info(new String(item.getPayload()));
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
                return result;
            }

            if (question.getCheckingMethod() == CheckingMethod.CONSOLE) {
                for (TestcaseInputEntry input : testcase.getInput()) {
                    stdinWrite.write(input.getValue().getBytes());
                    stdinWrite.write("\n".getBytes());
                }
                stdinWrite.close();
            }

            if (!resultCallback.awaitCompletion(question.getTimeLimit(), TimeUnit.MILLISECONDS)) {
                return result;
            }
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        } finally {
            dockerManager.stopContainer(containerId);
            dockerManager.removeContainer(containerId, true);
        }
        result.setIsExceedTimeLimit(false);

        return result;
    }
}
