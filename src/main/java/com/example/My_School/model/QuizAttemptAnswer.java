package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_attempt_answers")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizAttemptAnswer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Enumerated(EnumType.STRING)
    @Column(name = "chosen_option")
    private QuizQuestion.Option chosenOption;

    @Column(name = "is_correct")
    private boolean correct;
}