// package com.example.My_School.model;

// import jakarta.persistence.*;
// import lombok.*;
// import com.example.My_School.model.User;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
 
// @Entity
// @Table(name = "fee_payments")
// @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
// public class FeePayment {
 
//     @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "student_id", nullable = false)
//     private Student student;
 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "class_id", nullable = false)
//     private SchoolClass schoolClass;
 
//     @Column(name = "academic_year", nullable = false)
//     private String academicYear;
 
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private Term term;
 
//     @Column(name = "amount_due", nullable = false)
//     private BigDecimal amountDue;
 
//     @Column(name = "amount_paid", nullable = false)
//     private BigDecimal amountPaid = BigDecimal.ZERO;
 
//     @Column(name = "payment_date")
//     private LocalDateTime paymentDate;
 
//     @Enumerated(EnumType.STRING)
//     @Column(name = "payment_mode")
//     private PaymentMode paymentMode;
 
//     @Column(name = "receipt_number", unique = true)
//     private String receiptNumber;
 
//     private String remarks;
 
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "paid_by")
//     private User paidBy;
 
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private PaymentStatus status = PaymentStatus.PENDING;
 
//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;
 
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
 
//     @PrePersist  protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
//     @PreUpdate   protected void onUpdate() { updatedAt = LocalDateTime.now(); }
 
//     public enum Term          { TERM_1, TERM_2, TERM_3, ANNUAL }
//     public enum PaymentMode   { CASH, UPI, BANK_TRANSFER, CHEQUE }
//     public enum PaymentStatus { PENDING, PARTIAL, PAID }
// }
