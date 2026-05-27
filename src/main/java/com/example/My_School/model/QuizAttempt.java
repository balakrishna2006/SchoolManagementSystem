package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizAttempt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    private int score = 0;

    @Column(name = "total_questions")
    private int totalQuestions = 0;

    @Column(name = "correct_answers")
    private int correctAnswers = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizAttemptAnswer> answers;

    @PrePersist protected void onCreate() { if (startedAt == null) startedAt = LocalDateTime.now(); }

    public enum AttemptStatus { IN_PROGRESS, SUBMITTED, TIMED_OUT }
}
