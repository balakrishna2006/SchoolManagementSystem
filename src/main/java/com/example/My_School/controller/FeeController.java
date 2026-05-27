// package com.example.My_School.controller;

// import com.example.My_School.dto.FeePaymentDto;
// import com.example.My_School.service.FeeService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.*;
// import com.example.My_School.model.User;
 
// @RestController
// @RequiredArgsConstructor
// public class FeeController {
 
//     private final FeeService feeService;
 
//     @GetMapping("/admin/fees/class/{classId}")
//     public ResponseEntity<?> getFeesByClassAdmin(@PathVariable Long classId,
//                                                   @RequestParam String academicYear) {
//         return ResponseEntity.ok(feeService.getFeesByClass(classId, academicYear));
//     }
 
//     @GetMapping("/staff/fees/class/{classId}")
//     public ResponseEntity<?> getFeesByClass(@PathVariable Long classId,
//                                              @RequestParam String academicYear) {
//         return ResponseEntity.ok(feeService.getFeesByClass(classId, academicYear));
//     }
 
//     @PostMapping("/staff/fees/pay")
//     public ResponseEntity<?> recordPayment(@RequestBody FeePaymentDto dto,
//                                             @AuthenticationPrincipal UserDetails userDetails) {
//         Long staffId = ((User) userDetails).getId();
//         return ResponseEntity.ok(feeService.recordPayment(dto, staffId));
//     }
// }
