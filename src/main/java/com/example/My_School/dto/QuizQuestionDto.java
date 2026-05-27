package com.example.My_School.dto;

import lombok.Data;
import java.util.List;

// ---- Single question (sent to student during quiz — NO correct answer) ----
@Data
public class QuizQuestionDto {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int marks;
    private int questionOrder;
    // correctOption is intentionally omitted for student view
    private String correctOption;   // populated ONLY in results / staff view
    private String chosenOption;    // populated in results view
    private boolean correct;
}