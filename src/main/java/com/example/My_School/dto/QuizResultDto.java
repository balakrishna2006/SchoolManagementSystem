package com.example.My_School.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizResultDto {
    private Long attemptId;
    private Long quizId;
    private String quizTitle;
    private String subject;
    private int score;
    private int totalMarks;
    private int correctAnswers;
    private int totalQuestions;
    private int passingMarks;
    private boolean passed;
    private String status;
    private LocalDateTime submittedAt;
    private List<QuizQuestionDto> questions; // with correctOption + chosenOption filled
}