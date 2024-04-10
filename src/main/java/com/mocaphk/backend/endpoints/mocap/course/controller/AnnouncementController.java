package com.mocaphk.backend.endpoints.mocap.course.controller;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateAnnouncementInput;
import com.mocaphk.backend.endpoints.mocap.course.dto.UpdateAnnouncementInput;
import com.mocaphk.backend.endpoints.mocap.course.model.Announcement;
import com.mocaphk.backend.endpoints.mocap.course.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@SchemaMapping(typeName = "Announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    
    @QueryMapping(name = "announcement")
    public Announcement getAnnouncementById(@Argument Long id) {
        log.debug("getAnnouncementById: {}", id);
        return announcementService.getAnnouncementById(id);
    }

    @QueryMapping(name = "announcementsOfCourse")
    public List<Announcement> getAnnouncementsByCourseId(@Argument Long courseId) {
        log.debug("getAnnouncementsByCourseId: {}", courseId);
        return announcementService.getAnnouncementsByCourseId(courseId);
    }

    @QueryMapping(name = "announcementsOfCurrentUser")
    public List<Announcement> getAnnouncementsOfCurrentUser(Authentication authentication) {
        log.debug("getAnnouncementsOfCurrentUser");
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return announcementService.getAnnouncementsByUserId(authentication.getName());
    }

    @MutationMapping(name = "createAnnouncement")
    public Announcement createAnnouncement(Authentication authentication, @Argument CreateAnnouncementInput announcementInput) {
        log.debug("createAnnouncement: {}", announcementInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return announcementService.createAnnouncement(authentication.getName(), announcementInput);
    }

    @MutationMapping(name = "updateAnnouncement")
    public Announcement updateAnnouncement(@Argument Long id, @Argument UpdateAnnouncementInput announcementInput) {
        log.debug("updateAnnouncement: {}, {}", id, announcementInput);
        return announcementService.updateAnnouncement(id, announcementInput);
    }

    @MutationMapping(name = "deleteAnnouncement")
    public Announcement deleteAnnouncement(@Argument Long id) {
        log.debug("deleteAnnouncement: {}", id);
        return announcementService.deleteAnnouncement(id);
    }

    @MutationMapping(name = "markAnnouncementAsRead")
    public Boolean markAnnouncementAsRead(Authentication authentication, @Argument Long id) {
        log.debug("markAnnouncementAsRead: {}", id);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return announcementService.markAsReadByUser(id, authentication.getName());
    }

    @SchemaMapping
    public Boolean isReadByCurrentUser(Announcement announcement, Authentication authentication) {
        log.debug("isReadByCurrentUser: {}", announcement);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return announcementService.isReadByUser(announcement, authentication.getName());
    }
}
