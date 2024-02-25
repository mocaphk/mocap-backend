package com.mocaphk.backend.endpoints.keycloak.user.repository;

import com.mocaphk.backend.endpoints.keycloak.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}

