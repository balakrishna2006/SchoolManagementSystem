package com.example.My_School.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
 
@Data
public class AttendanceDto {
    private Long classId;
    private LocalDate date;
    private List<AttendanceRecord> records;
 
    @Data
    public static class AttendanceRecord {
        private Long studentId;
        private String status;  // "PRESENT" or "ABSENT"
    }
}
