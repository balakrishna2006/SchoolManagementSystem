package com.example.My_School.dto;

import lombok.Data;
 
@Data
public class AuthRequest {
    private String username;
    private String password;
}