package com.example.My_School.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// ---- Staff creates a quiz ----
@Data
public class QuizCreateDto {
    private String title;
    private String description;
    private Long classId;
    private String subject;
    private int durationMins;
    private int totalMarks;
    private int passingMarks;
    private LocalDateTime scheduledAt;
    private LocalDateTime expiresAt;
    private String status;   // DRAFT or PUBLISHED
    private List<QuestionInput> questions;

    @Data
    public static class QuestionInput {
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctOption;   // "A" | "B" | "C" | "D"
        private int marks;
    }
}