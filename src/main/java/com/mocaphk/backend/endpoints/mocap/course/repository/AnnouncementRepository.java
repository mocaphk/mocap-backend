package com.mocaphk.backend.endpoints.mocap.course.repository;

import com.mocaphk.backend.endpoints.mocap.course.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourseId(Long courseId);

    List<Announcement> findByCourseIdIn(Collection<Long> courseIds);
}
