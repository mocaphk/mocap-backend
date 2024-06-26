package com.mocaphk.backend.endpoints.mocap.user.service;

import com.mocaphk.backend.endpoints.mocap.course.model.CourseUser;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.dto.GetUserOutput;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class MocapUserService {
    private final CourseUserRepository courseUserRepository;
    private final MocapUserRepository mocapUserRepository;

    public List<GetUserOutput> getAllUsers() {
        List<MocapUser> users = mocapUserRepository.findAll();
        return users.stream().map(user -> new GetUserOutput(user.getId(), user.getUsername())).toList();
    }

    public List<MocapUser> getMocapUsersByCourseId(Long courseId) {
        return courseUserRepository.findByCourseId(courseId).stream()
                .map(CourseUser::getUser)
                .distinct()
                .toList();
    }
}
