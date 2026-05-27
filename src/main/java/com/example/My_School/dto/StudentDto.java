package com.example.My_School.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
 
@Data
public class StudentDto {
    private Long id;
 
    @NotBlank
    private String rollNumber;
 
    @NotBlank
    private String fullName;
 
    private LocalDate dateOfBirth;
    private String gender;
    private String guardianName;
    private String contactPhone;
    private String address;
 
    @NotNull
    private Long classId;
    
    private String generatedPassword;
    private String className;
    private LocalDate admissionDate;
}
