package com.mocaphk.backend;

import com.github.dockerjava.api.model.Frame;
import com.mocaphk.backend.components.DockerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
                "for x in range(10):\n\tprint(x)\nprint(\"Hello World\")\n",
                "/"
        );
        assertThat(StringUtils.isNotBlank(tempFileName)).isTrue();

        // this is an async task, so need to wait for it to finish
        boolean isExecSuccess = dockerManager.execCommand(
                containerId,
                dockerManager.new AutoCleanupCallback(containerId) {
                    @Override
                    public void onNext(Frame item) {
                        log.info(new String(item.getPayload()).trim());
                    }
                },
                "/bin/sh", "-c", String.format("python /%s", tempFileName)
        );
        assertThat(isExecSuccess).isTrue();

        log.info("Now sleep for 3 seconds");
        Thread.sleep(3000);
        log.info("Done sleeping");
    }
}
