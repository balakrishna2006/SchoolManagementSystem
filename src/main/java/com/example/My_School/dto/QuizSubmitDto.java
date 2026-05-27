package com.example.My_School.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// ---- Student submits answers ----
@Data
public class QuizSubmitDto {
    private Long quizId;
    private Long attemptId;
    // Map of questionId -> chosen option ("A"/"B"/"C"/"D")
    private Map<Long, String> answers;
}