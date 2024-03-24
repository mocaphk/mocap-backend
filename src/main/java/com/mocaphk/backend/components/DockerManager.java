package com.mocaphk.backend.components;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.Duration;

/**
 * This class handles all Docker related operations
 */
@Component
@Slf4j
public class DockerManager {

    @RequiredArgsConstructor
    public class AutoCleanupCallback extends ResultCallback.Adapter<Frame> {
        private final String containerId;
        private boolean isClosed = false;

        @Override
        public void close() throws IOException {
            if (!isClosed) {
                isClosed = true;
                if (!stopContainer(containerId) || !removeContainer(containerId, true)) {
                    log.warn("Failed to stop and remove container " + containerId);
                }
            }
            super.close();
        }
    }

    @Value("${docker.host}")
    private String dockerHost;

    @Value("${docker.tempDockerfileDir}")
    private String tempDockerfileDir;

    @Value("${docker.tempSourceDir}")
    private String tempSourceDir;

    private DockerClientConfig config;

    private DockerHttpClient httpClient;

    private DockerClient docker;

    @PostConstruct
    public void init() {
        config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(false)
                .build();
        httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        docker = DockerClientImpl.getInstance(config, httpClient);
    }

    /**
     * Build Docker image with persistent container which does not exit on its own.
     *
     * @param dockerFileContent The content of the Dockerfile
     * @return imageId
     */
    public String buildImageWithPersistentContainer(String dockerFileContent) {
        // TODO: if the dockerfile does not have bash, it will not work. Try withBuildArg
        return buildImage(dockerFileContent + "\nCMD exec /bin/bash -c \"trap : TERM INT; sleep infinity & wait\"\n");
    }

    /**
     * Build Docker image.
     *
     * @param dockerFileContent The content of the Dockerfile
     * @return imageId
     */
    public String buildImage(String dockerFileContent) {
        if (StringUtils.isBlank(dockerFileContent)) {
            return null;
        }

        File tempDockerFile = createTempFileFromString(dockerFileContent, "Dockerfile", null, tempDockerfileDir);
        if (tempDockerFile == null) {
            return null;
        }

        try {
            String imageId = docker.buildImageCmd(tempDockerFile).start().awaitImageId();
            tempDockerFile.delete();
            return imageId;
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return null;
    }

    public boolean removeImage(String imageId) {
        if (StringUtils.isBlank(imageId)) {
            return false;
        }

        try {
            docker.removeImageCmd(imageId).exec();
            return true;
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Create a Docker container.
     *
     * @param imageId The id of the image to create container from
     * @return containerId
     */
    public String createContainer(String imageId) {
        if (StringUtils.isBlank(imageId)) {
            return null;
        }

        try {
            return docker.createContainerCmd(imageId).exec().getId();
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return null;
    }

    /**
     * Remove a Docker container.
     *
     * @param containerId The id of the container to remove
     * @return true if success, false otherwise
     */
    public boolean removeContainer(String containerId, boolean force) {
        if (StringUtils.isBlank(containerId)) {
            return false;
        }

        try {
            docker.removeContainerCmd(containerId).withForce(force).exec();
            return true;
        } catch (Exception e) {
            log.error("Exception when removing container: ", e);
        }
        return false;
    }

    /**
     * Start a Docker container.
     *
     * @param containerId The id of the container to start
     * @return true if success, false otherwise
     */
    public boolean startContainer(String containerId) {
        if (StringUtils.isBlank(containerId)) {
            return false;
        }

        try {
            docker.startContainerCmd(containerId).exec();
            return true;
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Stop a Docker container.
     *
     * @param containerId The id of the container to stop
     * @return true if success, false otherwise
     */
    public boolean stopContainer(String containerId) {
        if (StringUtils.isBlank(containerId)) {
            return false;
        }

        try {
            docker.stopContainerCmd(containerId).exec();
            return true;
        } catch (Exception e) {
            log.error("Exception when stopping container: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copy file content to a Docker container.
     *
     * @param containerId The id of the container to copy file to
     * @param content The content of the file to copy
     * @param remotePath The path to copy the file to
     * @return fileName of the temp file created
     */
    public String copyFileToContainer(String containerId, String content, String remotePath) {
        if (StringUtils.isBlank(containerId) || StringUtils.isBlank(content) || StringUtils.isBlank(remotePath)) {
            return null;
        }

        File tempFile = createTempFileFromString(content, "temp", null, tempSourceDir);
        if (tempFile == null) {
            return null;
        }

        try {
            docker.copyArchiveToContainerCmd(containerId)
                    .withHostResource(tempFile.getAbsolutePath())
                    .withRemotePath(remotePath)
                    .exec();
            String fileName = tempFile.getName();
            tempFile.delete();
            return fileName;
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return null;
    }

    /**
     * Execute a command in a Docker container.
     *
     * @param containerId The id of the container to execute command in
     * @param stdin The input stream to the command
     * @param callback The callback to handle the status and output of the command
     * @param command The command to execute
     * @return true if success, false otherwise
     */
    public ResultCallback.Adapter<Frame> execCommand(String containerId, InputStream stdin, ResultCallback.Adapter<Frame> callback, String... command) {
        if (StringUtils.isBlank(containerId) || callback == null || command == null || command.length == 0) {
            return null;
        }

        try {
            var execCreateCmd = docker.execCreateCmd(containerId)
                   .withCmd(command)
                   .withAttachStdout(true)
                   .withAttachStderr(true);
            if (stdin != null) {
                execCreateCmd = execCreateCmd.withAttachStdin(true);
            }

            var execStartCmd = docker.execStartCmd(execCreateCmd.exec().getId());
            if (stdin != null) {
                execStartCmd.withStdIn(stdin);
            }
            return execStartCmd.exec(callback);
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return null;
    }

    // https://stackoverflow.com/questions/7083698/create-a-file-object-in-memory-from-a-string-in-java
    private File createTempFileFromString(String content, String prefix, String suffix, String directory) {
        try {
            File tempFile = File.createTempFile(prefix, suffix, new File(directory));
            tempFile.deleteOnExit();

            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            out.write(content);
            out.close();

            return tempFile;
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
        return null;
    }

    public DockerClient getDockerClient() {
        return docker;
    }
}
