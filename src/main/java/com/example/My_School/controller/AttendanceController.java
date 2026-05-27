package com.example.My_School.controller;

import com.example.My_School.dto.AttendanceDto;
import com.example.My_School.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.My_School.model.User;
 
import java.time.LocalDate;
 
@RestController
@RequiredArgsConstructor
public class AttendanceController {
 
    private final AttendanceService attendanceService;
 
    @GetMapping("/admin/attendance/class/{classId}")
    public ResponseEntity<?> getAttendance(@PathVariable Long classId,
                                            @RequestParam String date) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceForClass(classId, LocalDate.parse(date)));
    }
 
    @GetMapping("/staff/attendance/class/{classId}")
    public ResponseEntity<?> getAttendanceStaff(@PathVariable Long classId,
                                                  @RequestParam String date) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceForClass(classId, LocalDate.parse(date)));
    }
 
    @PostMapping("/staff/attendance")
    public ResponseEntity<?> saveAttendance(@RequestBody AttendanceDto dto,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Long staffId = ((User) userDetails).getId();
        attendanceService.saveAttendance(dto, staffId);
        return ResponseEntity.ok().build();
    }
 
    @GetMapping("/admin/attendance/summary/{studentId}")
    public ResponseEntity<?> summary(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceSummary(studentId));
    }
}
