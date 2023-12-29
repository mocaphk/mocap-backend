package com.mocaphk.backend.endpoints.dummy.repository;

import com.mocaphk.backend.endpoints.dummy.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
