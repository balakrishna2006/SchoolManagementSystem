package com.example.My_School.dto;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private String fullName;
    private Long userId;
    private Long classId;
    private String studentId;   // populated when role == STUDENT
}