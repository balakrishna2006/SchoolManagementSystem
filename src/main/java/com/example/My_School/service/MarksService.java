package com.example.My_School.service;

import com.example.My_School.dto.MarksDto;
import com.example.My_School.model.*;
import com.example.My_School.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarksService {

    private final MarksRepository marksRepo;
    private final StudentRepository studentRepo;
    private final SchoolClassRepository classRepo;

    public List<MarksDto> getByClass(Long classId, String academicYear) {
        return marksRepo.findBySchoolClassIdAndAcademicYear(classId, academicYear)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<MarksDto> getByStudent(Long studentId, String academicYear) {
        return marksRepo.findAllByStudentAndYear(studentId, academicYear)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public MarksDto saveOrUpdate(MarksDto dto) {
        Student student = studentRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        SchoolClass cls = classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Marks.ExamType examType = Marks.ExamType.valueOf(dto.getExamType());

        Marks marks = marksRepo.findByStudentIdAndExamTypeAndAcademicYear(
                dto.getStudentId(), examType, dto.getAcademicYear())
                .orElse(new Marks());
        int grade=cls.getGrade();
        marks.setStudent(student);
        marks.setSchoolClass(cls);
        marks.setExamType(
                Marks.ExamType.valueOf(dto.getExamType().toUpperCase()));
        marks.setAcademicYear(dto.getAcademicYear());
        marks.setTelugu(dto.getTelugu());
        marks.setHindi(dto.getHindi());
        marks.setEnglish(dto.getEnglish());
        marks.setMaths(dto.getMaths());
        marks.setScience(dto.getScience());
        marks.setSocial(dto.getSocial());
        marks.setBiology((grade == 10 || grade == 9 || grade == 8) ? dto.getBiology() : null);
        marks.setTotalMarks(dto.getTotalMarks());
        marks.setAbacus((grade == 1 || grade == 2) ? dto.getAbacus() : null);
        marks.setIit((grade >= 3 && grade <= 8) ? dto.getIit() : null);
        return toDto(marksRepo.save(marks));
    }

    private MarksDto toDto(Marks m) {
        MarksDto dto = new MarksDto();
        dto.setId(m.getId());
        dto.setStudentId(m.getStudent().getId());
        dto.setStudentName(m.getStudent().getFullName());
        dto.setRollNumber(m.getStudent().getRollNumber());
        dto.setClassId(m.getSchoolClass().getId());
        dto.setExamType(m.getExamType().name());
        dto.setAcademicYear(m.getAcademicYear());
        dto.setTelugu(m.getTelugu());
        dto.setHindi(m.getHindi());
        dto.setEnglish(m.getEnglish());
        dto.setMaths(m.getMaths());
        dto.setScience(m.getScience());
        dto.setSocial(m.getSocial());
        dto.setBiology(m.getBiology());
        dto.setAbacus(m.getAbacus());
        dto.setIit(m.getIit());
        dto.setTotalMarks(m.getTotalMarks());
        return dto;
    }
}
