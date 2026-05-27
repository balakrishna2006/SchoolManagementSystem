package com.example.My_School.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
 
@Data
public class MarksDto {
    private Long id;
 
    @NotNull
    private Long studentId;
 
    @NotNull
    private Long classId;
 
    @NotNull
    private String examType;
 
    @NotNull
    private String academicYear;
 
    private BigDecimal telugu  = BigDecimal.ZERO;
    private BigDecimal hindi   = BigDecimal.ZERO;
    private BigDecimal english = BigDecimal.ZERO;
    private BigDecimal maths   = BigDecimal.ZERO;
    private BigDecimal science = BigDecimal.ZERO;
    private BigDecimal social  = BigDecimal.ZERO;
    private BigDecimal biology;
     private BigDecimal abacus;
    private BigDecimal iit; 
    private BigDecimal totalMarks;
 
    private String studentName;
    private String rollNumber;
}