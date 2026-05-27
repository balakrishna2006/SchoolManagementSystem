package com.example.My_School.service;

import com.example.My_School.model.Quiz;
import com.example.My_School.model.Quiz.QuizStatus;
import com.example.My_School.repository.QuizRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizScheduler {

    private final QuizRepository quizRepository;

    @Scheduled(fixedRate = 60000) // runs every minute
    public void manageQuizStatus() {

        LocalDateTime now = LocalDateTime.now();

        // Publish quizzes
        List<Quiz> publishQuizzes =
                quizRepository.findByStatusAndScheduledAtBefore(
                        QuizStatus.DRAFT,
                        now
                );

        for (Quiz q : publishQuizzes) {
            q.setStatus(QuizStatus.PUBLISHED);
        }

        // Close quizzes
        List<Quiz> closeQuizzes =
                quizRepository.findByStatusAndExpiresAtLessThanEqual(
                        QuizStatus.PUBLISHED,
                        now
                );

        for (Quiz q : closeQuizzes) {
            q.setStatus(QuizStatus.CLOSED);
        }

        quizRepository.saveAll(publishQuizzes);
        quizRepository.saveAll(closeQuizzes);
    }
}