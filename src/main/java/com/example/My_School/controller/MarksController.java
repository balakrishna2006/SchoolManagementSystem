package com.example.My_School.controller;

import com.example.My_School.dto.MarksDto;
import com.example.My_School.service.MarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequiredArgsConstructor
public class MarksController {
 
    private final MarksService marksService;
 
    // Admin: view marks by class
    @GetMapping("/admin/marks/class/{classId}")
    public ResponseEntity<?> getByClass(@PathVariable Long classId,
                                         @RequestParam String academicYear) {
        return ResponseEntity.ok(marksService.getByClass(classId, academicYear));
    }
 
    // Staff: enter/update marks
    @PostMapping("/staff/marks")
    public ResponseEntity<?> saveMarks(@RequestBody MarksDto dto) {
        return ResponseEntity.ok(marksService.saveOrUpdate(dto));
    }
 
    @GetMapping("/staff/marks/student/{studentId}")
    public ResponseEntity<?> getByStudent(@PathVariable Long studentId,
                                           @RequestParam String academicYear) {
        return ResponseEntity.ok(marksService.getByStudent(studentId, academicYear));
    }
}
