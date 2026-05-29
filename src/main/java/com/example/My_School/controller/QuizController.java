package com.example.My_School.controller;

import com.example.My_School.dto.*;
import com.example.My_School.model.User;
import com.example.My_School.service.AuthService;
import com.example.My_School.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final AuthService authService;

    // ===========================================================
    // STAFF endpoints
    // ===========================================================

    /** Create a new quiz with questions */
    @PostMapping("/staff/quizzes")
    public ResponseEntity<?> createQuiz(@RequestBody QuizCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId();
        return ResponseEntity.ok(quizService.createQuizWithQuestions(dto, userId));
    }

    /** List all quizzes for a class (staff view) */
    @GetMapping("/staff/quizzes/class/{classId}")
    public ResponseEntity<?> listQuizzes(@PathVariable Long classId) {
        return ResponseEntity.ok(quizService.getQuizzesByClass(classId));
    }

    /** Publish / close a quiz */
    @PutMapping("/staff/quizzes/{quizId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long quizId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(quizService.updateStatus(quizId, body.get("status")));
    }

    /** Get all student attempt results for a quiz */
    @GetMapping("/staff/quizzes/{quizId}/results")
    public ResponseEntity<?> getResults(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizResults(quizId));
    }

    /** Admin also can view quizzes and results */
    @GetMapping("/admin/quizzes/class/{classId}")
    public ResponseEntity<?> listQuizzesAdmin(@PathVariable Long classId) {
        return ResponseEntity.ok(quizService.getQuizzesByClass(classId));
    }

    @GetMapping("/admin/quizzes/{quizId}/results")
    public ResponseEntity<?> getResultsAdmin(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizResults(quizId));
    }

    /** Staff resets a student's portal password */
    @PostMapping("/staff/students/{studentId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long studentId,
            @RequestBody Map<String, String> body) {
        authService.resetStudentPassword(studentId, body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    // ===========================================================
    // STUDENT endpoints
    // ===========================================================

    /** List published quizzes for the student's class */
    @GetMapping("/student/quizzes")
    public ResponseEntity<?> studentListQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        User u = (User) userDetails;
        Long classId = u.getSchoolClass().getId();
        Long studentId = u.getStudentId();
        return ResponseEntity.ok(quizService.getPublishedQuizzesForStudent(classId, studentId));
    }

    /** Start a quiz (returns questions, no correct answers) */
    @PostMapping("/student/quizzes/{quizId}/start")
    public ResponseEntity<?> startQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("QUIZ START ENDPOINT HIT");
        String username = userDetails.getUsername();

        User u = authService.findByUsername(username);

        Long studentId = u.getStudentId();

        return ResponseEntity.ok(
                quizService.startQuiz(quizId, studentId));
    }

    /** Submit quiz answers */
    @PostMapping("/student/quizzes/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmitDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = ((User) userDetails).getStudentId();
        return ResponseEntity.ok(quizService.submitQuiz(dto, studentId));
    }

    /** View result of a submitted quiz */
    @GetMapping("/student/quizzes/{quizId}/result")
    public ResponseEntity<?> getResult(@PathVariable Long quizId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = ((User) userDetails).getStudentId();
        return ResponseEntity.ok(quizService.getResult(quizId, studentId));
    }

    @DeleteMapping("/staff/quizzes/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/staff/quizzes/{id}")
    public ResponseEntity<?> updateQuiz(
            @PathVariable Long id,
            @RequestBody QuizDto dto) {
        System.out.println("hello");
        return ResponseEntity.ok(
                quizService.updateQuiz(id, dto));
    }

    @GetMapping("/staff/quizzes/{id}")
    public ResponseEntity<?> getQuiz(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                quizService.getQuiz(id));
    }

}