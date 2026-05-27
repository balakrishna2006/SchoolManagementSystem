package com.example.My_School.service;

import com.example.My_School.dto.AttendanceDto;
import com.example.My_School.model.*;
import com.example.My_School.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class AttendanceService {
 
    private final AttendanceRepository attRepo;
    private final StudentRepository studentRepo;
    private final SchoolClassRepository classRepo;
    private final UserRepository userRepo;
 
    public List<Map<String, Object>> getAttendanceForClass(Long classId, LocalDate date) {
        List<Attendance> records = attRepo.findBySchoolClassIdAndAttDate(classId, date);
        List<Student> students = studentRepo.findBySchoolClassIdAndActiveTrue(classId);
 
        Map<Long, Attendance> attMap = records.stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a));
 
        return students.stream().map(s -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("studentId", s.getId());
            row.put("rollNumber", s.getRollNumber());
            row.put("fullName", s.getFullName());
            Attendance a = attMap.get(s.getId());
            row.put("status", a != null ? a.getStatus().name() : "PRESENT");
            row.put("attendanceId", a != null ? a.getId() : null);
            return row;
        }).collect(Collectors.toList());
    }
 
    @Transactional
    public void saveAttendance(AttendanceDto dto, Long staffId) {
        SchoolClass cls = classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        User staff = staffId != null ? userRepo.findById(staffId).orElse(null) : null;
 
        for (AttendanceDto.AttendanceRecord rec : dto.getRecords()) {
            Student student = studentRepo.findById(rec.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found: " + rec.getStudentId()));
 
            Attendance att = attRepo.findByStudentIdAndAttDate(rec.getStudentId(), dto.getDate())
                    .orElse(new Attendance());
 
            att.setStudent(student);
            att.setSchoolClass(cls);
            att.setAttDate(dto.getDate());
            att.setStatus(Attendance.Status.valueOf(rec.getStatus()));
            att.setMarkedBy(staff);
            attRepo.save(att);
        }
    }
 
    public Map<String, Object> getAttendanceSummary(Long studentId) {
        long present = attRepo.countPresentByStudent(studentId);
        long total   = attRepo.countTotalByStudent(studentId);
        Map<String, Object> summary = new HashMap<>();
        summary.put("studentId", studentId);
        summary.put("present", present);
        summary.put("absent", total - present);
        summary.put("total", total);
        summary.put("percentage", total > 0 ? Math.round((present * 100.0) / total) : 0);
        return summary;
    }
}