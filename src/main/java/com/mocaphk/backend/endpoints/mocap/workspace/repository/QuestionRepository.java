package com.mocaphk.backend.endpoints.mocap.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mocaphk.backend.endpoints.mocap.workspace.model.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q JOIN q.assignment a JOIN a.course c WHERE q.isPublic = true AND c.code = :code AND (q.title LIKE %:keyword% OR q.description LIKE %:keyword%)")
    List<Question> searchPublicQuestions(@Param("code") String code, @Param("keyword") String keyword);
}
