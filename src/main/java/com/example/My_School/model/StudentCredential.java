package com.example.My_School.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_credentials")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rollNumber;
    private String fullName;
    private String generatedPassword;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;
}   