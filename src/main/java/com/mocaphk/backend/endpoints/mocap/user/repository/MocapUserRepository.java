package com.mocaphk.backend.endpoints.mocap.user.repository;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MocapUserRepository extends JpaRepository<MocapUser, String> {
    MocapUser findOneByUsername(String username);
}