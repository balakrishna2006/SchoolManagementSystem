package com.example.My_School.repository;

import com.example.My_School.model.Quiz;
import com.example.My_School.model.Quiz.QuizStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySchoolClassIdAndStatusOrderByCreatedAtDesc(Long classId, Quiz.QuizStatus status);

    List<Quiz> findBySchoolClassIdOrderByCreatedAtDesc(Long classId);

    List<Quiz> findByStatusAndScheduledAtBefore(
            QuizStatus status,
            LocalDateTime time);

    List<Quiz> findByStatusAndExpiresAtLessThanEqual(
            Quiz.QuizStatus status,
            LocalDateTime time);

    List<Quiz> findByStatus(QuizStatus status);

    List<Quiz> findBySchoolClassId(Long classId);
}