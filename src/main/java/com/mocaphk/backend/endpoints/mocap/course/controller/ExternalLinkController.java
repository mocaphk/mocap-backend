package com.mocaphk.backend.endpoints.mocap.course.controller;

import com.mocaphk.backend.endpoints.mocap.course.dto.CreateExternalLinkInput;
import com.mocaphk.backend.endpoints.mocap.course.model.ExternalLink;
import com.mocaphk.backend.endpoints.mocap.course.service.ExternalLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ExternalLinkController {
    private final ExternalLinkService externalLinkService;

    @MutationMapping(name = "createExternalLink")
    public ExternalLink createExternalLink(Authentication authentication, @Argument CreateExternalLinkInput externalLinkInput) {
        log.debug("createExternalLink: {}", externalLinkInput);
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return externalLinkService.createExternalLink(externalLinkInput, authentication.getName());
    }
}
