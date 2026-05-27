package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.My_School.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "attendance")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Attendance {
 
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
 
    @Column(name = "att_date", nullable = false)
    private LocalDate attDate;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PRESENT;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private User markedBy;
 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
 
    public enum Status { PRESENT, ABSENT }
}
