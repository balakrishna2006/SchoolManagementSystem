package com.example.My_School.controller;

import com.example.My_School.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {
 
    private final SchoolClassRepository classRepo;
 
    @GetMapping
    public ResponseEntity<?> getAllClasses() {
        return ResponseEntity.ok(classRepo.findAllByOrderByGradeAsc());
    }
}