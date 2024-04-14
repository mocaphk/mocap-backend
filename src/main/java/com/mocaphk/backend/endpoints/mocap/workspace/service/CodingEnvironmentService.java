package com.mocaphk.backend.endpoints.mocap.workspace.service;

import com.mocaphk.backend.components.DockerManager;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.CreateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.dto.UpdateCodingEnvironmentInput;
import com.mocaphk.backend.endpoints.mocap.workspace.model.CodingEnvironment;
import com.mocaphk.backend.endpoints.mocap.workspace.repository.CodingEnvironmentRepository;

import com.mocaphk.backend.utils.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodingEnvironmentService {
    private final CodingEnvironmentRepository codingEnvironmentRepository;
    private final DockerManager dockerManager;

    public CodingEnvironment getCodingEnvironmentById(Long id) {
        return codingEnvironmentRepository.findById(id).orElse(null);
    }

    public List<CodingEnvironment> getCodingEnvironmentByAssignmentId(Long id) {
        return codingEnvironmentRepository.findByIsBuiltTrueAndCourse_Assignments_IdOrCourseIdNullOrderByNameAsc(id);
    }

    public CodingEnvironment createCodingEnvironment(CreateCodingEnvironmentInput input) {
        CodingEnvironment codingEnvironment = new CodingEnvironment();
        codingEnvironment.setName(input.name());
        codingEnvironment.setDescription(input.description());
        codingEnvironment.setDockerfile(input.dockerfile());
        codingEnvironment.setIsBuilt(false);
        codingEnvironment.setDockerImageId(null);
        codingEnvironment.setCreatedAt(DateUtils.now());
        codingEnvironment.setCourseId(input.courseId());
        codingEnvironmentRepository.save(codingEnvironment);
        return codingEnvironment;
    }

    public CodingEnvironment updateCodingEnvironment(Long id, UpdateCodingEnvironmentInput input) {
        CodingEnvironment codingEnvironment = getCodingEnvironmentById(id);
        if (codingEnvironment == null) {
            return null;
        }

        if (StringUtils.isNotBlank(input.name())) {
            codingEnvironment.setName(input.name());
        }
        if (StringUtils.isNotBlank(input.description())) {
            codingEnvironment.setDescription(input.description());
        }
        if (StringUtils.isNotBlank(input.dockerfile())) {
            codingEnvironment.setDockerfile(input.dockerfile());

            if (StringUtils.isNotBlank(codingEnvironment.getDockerImageId())) {
                dockerManager.removeImage(codingEnvironment.getDockerImageId());
                codingEnvironment.setDockerImageId(null);
                codingEnvironment.setIsBuilt(false);
            }
        }
        codingEnvironmentRepository.save(codingEnvironment);
        return codingEnvironment;
    }

    public CodingEnvironment deleteCodingEnvironment(Long id) {
        CodingEnvironment codingEnvironment = getCodingEnvironmentById(id);
        if (codingEnvironment == null) {
            return null;
        }

        if (StringUtils.isNotBlank(codingEnvironment.getDockerImageId())) {
            dockerManager.removeImage(codingEnvironment.getDockerImageId());
        }
        codingEnvironmentRepository.deleteById(id);
        return codingEnvironment;
    }

    public CodingEnvironment buildCodingEnvironment(Long id) {
        CodingEnvironment codingEnvironment = getCodingEnvironmentById(id);
        if (codingEnvironment == null) {
            return null;
        }

        String dockerImageId = dockerManager.buildImageWithPersistentContainer(codingEnvironment.getDockerfile());
        if (StringUtils.isBlank(dockerImageId)) {
            return null;
        }
        codingEnvironment.setDockerImageId(dockerImageId);
        codingEnvironment.setIsBuilt(true);
        codingEnvironmentRepository.save(codingEnvironment);
        return codingEnvironment;
    }

}
