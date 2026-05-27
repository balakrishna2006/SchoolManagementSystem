package com.example.My_School.controller;

import com.example.My_School.dto.StudentDto;
import com.example.My_School.model.Quiz;
import com.example.My_School.model.User;
import com.example.My_School.repository.QuizRepository;
import com.example.My_School.service.AttendanceService;
import com.example.My_School.service.MarksService;
import com.example.My_School.service.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudentController {

        private final StudentService studentService;
        private final MarksService marksService;
        private final AttendanceService attendanceService;
        // ==========================================
        // STAFF APIs
        // ==========================================

        @GetMapping("/staff/students/class/{classId}")
        public ResponseEntity<?> getByClass(@PathVariable Long classId) {
                return ResponseEntity.ok(
                                studentService.getByClass(classId));
        }

        @PostMapping("/staff/students")
        public ResponseEntity<?> create(@RequestBody StudentDto dto) {
                return ResponseEntity.ok(
                                studentService.create(dto));
        }

        @PutMapping("/staff/students/{id}")
        public ResponseEntity<?> update(
                        @PathVariable Long id,
                        @Valid @RequestBody StudentDto dto) {

                return ResponseEntity.ok(
                                studentService.update(id, dto));
        }

        @DeleteMapping("/staff/students/{id}")
        public ResponseEntity<?> delete(@PathVariable Long id) {

                studentService.delete(id);

                return ResponseEntity.ok(
                                "Student deleted successfully");
        }

        // ==========================================
        // STUDENT APIs
        // ==========================================

        @GetMapping("/student/profile")
        public ResponseEntity<?> getMyProfile(
                        @AuthenticationPrincipal User user) {

                Long studentId = Long.valueOf(
                                user.getStudentId());

                return ResponseEntity.ok(
                                studentService.getStudentById(studentId));
        }

        @GetMapping("/student/marks")
        public ResponseEntity<?> getMyMarks(
                        @AuthenticationPrincipal User user,
                        @RequestParam String academicYear) {

                Long studentId = Long.valueOf(
                                user.getStudentId());

                return ResponseEntity.ok(
                                marksService.getByStudent(
                                                studentId,
                                                academicYear));
        }

        @GetMapping("/student/attendance")
        public ResponseEntity<?> getMyAttendance(
                        @AuthenticationPrincipal User user) {

                Long studentId = Long.valueOf(
                                user.getStudentId());

                return ResponseEntity.ok(
                                attendanceService.getAttendanceSummary(studentId));
        }

        
}