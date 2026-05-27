package com.example.My_School.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizDto {
    private Long id;
    private String title;
    private String description;
    private Long classId;
    private String className;
    private String subject;
    private int durationMins;
    private int totalMarks;
    private int passingMarks;
    private LocalDateTime scheduledAt;
    private LocalDateTime expiresAt;
    private String status;
    private int questionCount;
    private Boolean attempted;
    private Boolean submitted;

    // Full question list — populated by getQuiz() for staff edit view
    private List<QuizQuestionDto> questions;

    // Student view extras
    private String attemptStatus;
    private Integer score;
    private Integer correctAnswers;
}