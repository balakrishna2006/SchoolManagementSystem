package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "marks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private ExamType examType;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    private BigDecimal telugu = BigDecimal.ZERO;
    private BigDecimal hindi = BigDecimal.ZERO;
    private BigDecimal english = BigDecimal.ZERO;
    private BigDecimal maths = BigDecimal.ZERO;
    private BigDecimal science = BigDecimal.ZERO;
    private BigDecimal social = BigDecimal.ZERO;
    private BigDecimal biology; // null if not Class 10
    private BigDecimal abacus;
    private BigDecimal iit;

    @Column(name = "total_marks")
    private BigDecimal totalMarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ExamType {
        FA1,
        FA2,
        FA3,
        SA1,
        FA4,
        SA2
    }
}
