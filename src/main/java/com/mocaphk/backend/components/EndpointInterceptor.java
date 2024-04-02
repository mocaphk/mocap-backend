package com.mocaphk.backend.components;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import com.mocaphk.backend.endpoints.keycloak.user.repository.UserRepository;
import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import com.mocaphk.backend.endpoints.mocap.user.repository.MocapUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class EndpointInterceptor implements HandlerInterceptor {

    private final MocapUserRepository mocapUserRepository;
    private final UserRepository userRepository;

    @Autowired
    public EndpointInterceptor(MocapUserRepository mocapUserRepository, UserRepository userRepository) {
        this.mocapUserRepository = mocapUserRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                MocapUser mocapUser = new MocapUser(user);
                mocapUserRepository.save(mocapUser);
            }
        }

        return true;
    }
}