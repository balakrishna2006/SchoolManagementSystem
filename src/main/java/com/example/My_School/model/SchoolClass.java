package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "classes")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SchoolClass {
 
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, unique = true)
    private String name;
 
    @Column(nullable = false)
    private int grade;
 
    private String section;
 
    @Column(name = "fee_amount", nullable = false)
    private BigDecimal feeAmount;
 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
