package com.example.My_School.repository;

import com.example.My_School.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    Optional<QuizAttempt> findByQuizIdAndStudentId(Long quizId, Long studentId);
    List<QuizAttempt> findByStudentIdOrderByStartedAtDesc(Long studentId);
    List<QuizAttempt> findByQuizId(Long quizId);
}