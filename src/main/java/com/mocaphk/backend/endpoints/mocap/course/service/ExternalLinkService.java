package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateExternalLinkInput;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import com.mocaphk.backend.endpoints.mocap.course.model.ExternalLink;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.ExternalLinkRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import com.mocaphk.backend.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalLinkService {
    private final ExternalLinkRepository externalLinkRepository;
    private final CourseRepository courseRepository;
    private final MocapUserRepository mocapUserRepository;


    public ExternalLink createExternalLink(CreateExternalLinkInput input, String userId) {
        MocapUser user = mocapUserRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        Course course = courseRepository.findById(input.courseId()).orElse(null);

        if (course == null) {
            return null;
        }

        ExternalLink externalLink = new ExternalLink();
        externalLink.setCourse(course);
        externalLink.setTitle(input.title());
        externalLink.setDescription(input.description());
        externalLink.setLink(input.link());
        externalLink.setCreatedBy(user);
        String now = DateUtils.now();
        externalLink.setCreatedAt(now);
        externalLink.setUpdatedAt(now);

        externalLinkRepository.save(externalLink);

        return externalLink;
    }
}
