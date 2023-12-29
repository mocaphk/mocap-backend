package com.mocaphk.backend.endpoints.dummy.repository;

import com.mocaphk.backend.endpoints.dummy.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
