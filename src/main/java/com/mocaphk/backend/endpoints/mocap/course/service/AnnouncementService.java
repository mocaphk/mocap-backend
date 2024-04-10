package com.mocaphk.backend.endpoints.mocap.course.service;

import com.mocaphk.backend.components.MocapMailSender;
import com.mocaphk.backend.endpoints.mocap.course.dto.CreateAnnouncementInput;
import com.mocaphk.backend.endpoints.mocap.course.dto.UpdateAnnouncementInput;
import com.mocaphk.backend.endpoints.mocap.course.model.Announcement;
import com.mocaphk.backend.endpoints.mocap.course.model.Course;
import com.mocaphk.backend.endpoints.mocap.course.repository.AnnouncementRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseRepository;
import com.mocaphk.backend.endpoints.mocap.course.repository.CourseUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import com.mocaphk.backend.endpoints.mocap.user.service.MocapUserService;
import com.mocaphk.backend.utils.DateUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final MocapUserRepository mocapUserRepository;
    private final CourseUserService courseUserService;
    private final MocapUserService mocapUserService;
    private final MocapMailSender mocapMailSender;

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }

    public List<Announcement> getAnnouncementsByCourseId(Long courseId) {
        return announcementRepository.findByCourseId(courseId);
    }

    public List<Announcement> getAnnouncementsByUserId(String userId) {
        List<Course> courses = courseUserService.getCoursesByUserId(userId);
        return announcementRepository.findByCourseIdIn(courses.stream().map(Course::getId).toList());
    }

    public Announcement createAnnouncement(String userId, CreateAnnouncementInput input) {
        Announcement announcement = new Announcement();
        announcement.setTitle(input.title());
        announcement.setContent(input.content());
        announcement.setCreatedAt(DateUtils.now());
        announcement.setUpdatedAt(DateUtils.now());
        announcement.setCreatedById(userId);
        announcement.setCourseId(input.courseId());
        announcement.setReadBy(new HashSet<>());
        sendAnnouncementMail(announcement);
        return announcementRepository.save(announcement);
    }

    public Announcement updateAnnouncement(Long id, UpdateAnnouncementInput input) {
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        if (announcement == null) {
            return null;
        }

        if (StringUtils.isNotBlank(input.title())) {
            announcement.setTitle(input.title());
        }
        if (StringUtils.isNotBlank(input.content())) {
            announcement.setContent(input.content());
        }
        return announcementRepository.save(announcement);
    }

    public Announcement deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        announcementRepository.deleteById(id);
        return announcement;
    }

    public Boolean isReadByUser(Announcement announcement, String userId) {
        return announcement.getReadBy().stream().anyMatch(user -> user.getId().equals(userId));
    }

    public Boolean markAsReadByUser(Long id, String userId) {
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        if (announcement == null) {
            return false;
        }

        MocapUser user = mocapUserRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        announcement.getReadBy().add(user);
        announcementRepository.save(announcement);
        return true;
    }

    private void sendAnnouncementMail(Announcement announcement) {
        MocapUser creator = mocapUserRepository.findById(announcement.getCreatedById()).orElse(null);
        if (creator == null) {
            return;
        }
        
        Course course = courseRepository.findById(announcement.getCourseId()).orElse(null);
        if (course == null) {
            return;
        }

        String creatorName = creator.getUsername();
        String subject = String.format("[MOCAP] %s: New Announcement form %s: %s",
                course.getCode(),
                creatorName,
                announcement.getTitle()
        );
        String body = String.format("New announcement: %s\n\nContent: %s",
                announcement.getTitle(),
                announcement.getContent()
        );

        for (MocapUser user : mocapUserService.getMocapUsersByCourseId(course.getId())) {
            try {
                mocapMailSender.sendMail(
                        String.format("%s [via MOCAP]", creatorName),
                        user.getEmail(),
                        subject,
                        body
                );
            } catch (Exception e) {
                log.error("Failed to send AnnouncementMail to user: " + user.getId(), e);
            }
        }
    }
}
