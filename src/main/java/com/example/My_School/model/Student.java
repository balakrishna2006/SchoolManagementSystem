package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "students")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", nullable = false, unique = true)
    private String rollNumber;



    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "contact_phone")
    private String contactPhone;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    /** BCrypt-hashed password for student portal login. Default = BCrypt('student123') */
    @Column(name = "login_password")
    private String loginPassword;

    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (admissionDate == null) admissionDate = LocalDate.now();
        
        if (loginPassword == null || loginPassword.isBlank()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            loginPassword = encoder.encode("student123");
        }
    }

    public enum Gender { MALE, FEMALE, OTHER }

   
}