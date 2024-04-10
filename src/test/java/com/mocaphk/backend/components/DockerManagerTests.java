package com.mocaphk.backend.components;

import com.github.dockerjava.api.model.Frame;
import com.mocaphk.backend.components.DockerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class DockerManagerTests {
    @Autowired
    private DockerManager dockerManager;

    @Test
    public void testSimplePythonScript() throws Exception {
        String imageId = dockerManager.buildImageWithPersistentContainer("FROM python:3\n");
        assertThat(StringUtils.isNotBlank(imageId)).isTrue();

        String containerId = dockerManager.createContainer(imageId);
        assertThat(StringUtils.isNotBlank(containerId)).isTrue();

        boolean isStartSuccess = dockerManager.startContainer(containerId);
        assertThat(isStartSuccess).isTrue();

        String tempFileName = dockerManager.copyFileToContainer(
                containerId,
                "for x in range(10):\n\tprint(x)\ny = input(\"Enter a number: \")\nprint(\"You entered:\", y)\n",
                "/"
        );
        assertThat(StringUtils.isNotBlank(tempFileName)).isTrue();

        PipedOutputStream stdin = new PipedOutputStream();

        // this is an async task, so need to wait for it to finish
        var resultCallback = dockerManager.execCommand(
                containerId,
                new PipedInputStream(stdin),
                dockerManager.new AutoCleanupCallback(containerId) {
                    @Override
                    public void onNext(Frame item) {
                        log.info(item.toString());
                    }
                },
                "/bin/sh", "-c", String.format("python /%s", tempFileName)
        );
        assertThat(resultCallback).isNotNull();

        assertThat(resultCallback.awaitStarted(3, TimeUnit.SECONDS)).isTrue();
        stdin.write("5\n".getBytes());
        stdin.close();
        assertThat(resultCallback.awaitCompletion(3, TimeUnit.SECONDS)).isTrue();

        boolean isRemoveSuccess = dockerManager.removeImage(imageId);
        assertThat(isRemoveSuccess).isTrue();
    }
}
