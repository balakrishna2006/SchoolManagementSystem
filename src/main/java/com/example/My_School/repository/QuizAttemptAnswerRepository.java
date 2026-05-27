package com.example.My_School.repository;

import com.example.My_School.model.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizAttemptAnswerRepository extends JpaRepository<QuizAttemptAnswer, Long> {
    List<QuizAttemptAnswer> findByAttemptId(Long attemptId);
}