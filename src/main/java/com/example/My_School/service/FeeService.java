// package com.example.My_School.service;

// import com.example.My_School.dto.FeePaymentDto;
// import com.example.My_School.model.*;
// import com.example.My_School.repository.*;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
 
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.stream.Collectors;
 
// @Service
// @RequiredArgsConstructor
// public class FeeService {
 
//     private final FeePaymentRepository feeRepo;
//     private final StudentRepository studentRepo;
//     private final SchoolClassRepository classRepo;
//     private final UserRepository userRepo;
 
//     public List<FeePaymentDto> getFeesByClass(Long classId, String academicYear) {
//         return feeRepo.findBySchoolClassIdAndAcademicYear(classId, academicYear)
//                 .stream().map(this::toDto).collect(Collectors.toList());
//     }
 
//     public List<FeePaymentDto> getFeesByStudent(Long studentId, String academicYear) {
//         return feeRepo.findByStudentIdAndAcademicYear(studentId, academicYear)
//                 .stream().map(this::toDto).collect(Collectors.toList());
//     }
 
//     @Transactional
//     public FeePaymentDto recordPayment(FeePaymentDto dto, Long staffId) {
//         FeePayment fee = feeRepo.findById(dto.getId())
//                 .orElseThrow(() -> new RuntimeException("Fee record not found"));
 
//         BigDecimal newPaid = fee.getAmountPaid().add(dto.getAmountPaid());
//         fee.setAmountPaid(newPaid);
//         fee.setPaymentDate(LocalDateTime.now());
//         fee.setPaymentMode(FeePayment.PaymentMode.valueOf(dto.getPaymentMode()));
//         fee.setRemarks(dto.getRemarks());
 
//         if (staffId != null) {
//             userRepo.findById(staffId).ifPresent(fee::setPaidBy);
//         }
 
//         if (newPaid.compareTo(fee.getAmountDue()) >= 0) {
//             fee.setStatus(FeePayment.PaymentStatus.PAID);
//         } else if (newPaid.compareTo(BigDecimal.ZERO) > 0) {
//             fee.setStatus(FeePayment.PaymentStatus.PARTIAL);
//         }
 
//         // Generate receipt number
//         if (fee.getReceiptNumber() == null) {
//             fee.setReceiptNumber("RCP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//         }
 
//         return toDto(feeRepo.save(fee));
//     }
 
//     private FeePaymentDto toDto(FeePayment f) {
//         FeePaymentDto dto = new FeePaymentDto();
//         dto.setId(f.getId());
//         dto.setStudentId(f.getStudent().getId());
//         dto.setStudentName(f.getStudent().getFullName());
//         dto.setRollNumber(f.getStudent().getRollNumber());
//         dto.setClassId(f.getSchoolClass().getId());
//         dto.setClassName(f.getSchoolClass().getName());
//         dto.setAcademicYear(f.getAcademicYear());
//         dto.setTerm(f.getTerm().name());
//         dto.setAmountDue(f.getAmountDue());
//         dto.setAmountPaid(f.getAmountPaid());
//         dto.setBalance(f.getAmountDue().subtract(f.getAmountPaid()));
//         dto.setPaymentDate(f.getPaymentDate());
//         dto.setPaymentMode(f.getPaymentMode() != null ? f.getPaymentMode().name() : null);
//         dto.setReceiptNumber(f.getReceiptNumber());
//         dto.setRemarks(f.getRemarks());
//         dto.setStatus(f.getStatus().name());
//         return dto;
//     }
// }
