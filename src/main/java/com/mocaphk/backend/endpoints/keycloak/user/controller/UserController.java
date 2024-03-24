package com.mocaphk.backend.endpoints.keycloak.user.controller;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import com.mocaphk.backend.endpoints.keycloak.user.service.UserService;
import com.mocaphk.backend.enums.Roles;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @QueryMapping(name = "user")
    public User getUser(Authentication authentication) {
        log.debug("getUser");
        if (!authentication.isAuthenticated()) {
            return null;
        }
        return userService.getUserById(authentication.getName());
    }

    @QueryMapping(name = "userById")
    public User getUserById(@Argument String id) {
        log.debug("getUserById: {}", id);
        return userService.getUserById(id);
    }

    @RolesAllowed({ Roles.ADMIN })
    @QueryMapping(name = "allUsers")
    public List<User> getAllUsers() {
        log.debug("getUserById");
        return userService.getAllUsers();
    }

}
