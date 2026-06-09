package com.example.My_School.service;

import com.example.My_School.dto.StudentDto;
import com.example.My_School.model.Student;
import com.example.My_School.model.SchoolClass;
import com.example.My_School.repository.AttendanceRepository;
import com.example.My_School.repository.MarksRepository;
import com.example.My_School.repository.SchoolClassRepository;
import com.example.My_School.repository.StudentRepository;
import com.example.My_School.repository.UserRepository;
import com.example.My_School.model.StudentCredential;
import com.example.My_School.model.User;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.My_School.repository.StudentCredentialRepository;
import com.example.My_School.model.StudentCredential;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class StudentService {
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepo;
    private final SchoolClassRepository classRepo;
    private final StudentCredentialRepository credentialRepo;
    private final UserRepository userRepo;

    private final StudentCredentialRepository studentCredentialRepo;
    private final MarksRepository marksRepo;
    private final AttendanceRepository attendanceRepo;
    

    public List<StudentDto> getByClass(Long classId) {
        return studentRepo.findBySchoolClassIdAndActiveTrue(classId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public StudentDto create(StudentDto dto) {

        if (studentRepo.existsByRollNumber(dto.getRollNumber())) {
            throw new RuntimeException(
                    "Student with roll number " +
                            dto.getRollNumber() +
                            " already exists");
        }

        SchoolClass cls = classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // Generate random password
        String rawPassword = generateRandomPassword();

        Student s = Student.builder()
                .rollNumber(dto.getRollNumber())
                .fullName(dto.getFullName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender() != null
                        ? Student.Gender.valueOf(dto.getGender())
                        : null)
                .guardianName(dto.getGuardianName())
                .contactPhone(dto.getContactPhone())
                .address(dto.getAddress())
                .schoolClass(cls)
                .active(true)
                .admissionDate(java.time.LocalDate.now())

                // Save encrypted password
                .loginPassword(passwordEncoder.encode(rawPassword))
                .build();

        Student savedStudent = studentRepo.save(s);
        StudentCredential credential = StudentCredential.builder()
                .rollNumber(savedStudent.getRollNumber())
                .fullName(s.getFullName())
                .generatedPassword(rawPassword)
                .schoolClass(cls)
                .build();

        credentialRepo.save(credential);

        StudentDto response = toDto(savedStudent);

        return response;
    }

    @Transactional
    public StudentDto update(Long id, StudentDto dto) {
        Student s = studentRepo.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        
        SchoolClass cls = classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        s.setFullName(dto.getFullName());
        String oldRollNumber = s.getRollNumber();
        s.setRollNumber(dto.getRollNumber());
        s.setDateOfBirth(dto.getDateOfBirth());
        s.setGender(dto.getGender() != null ? Student.Gender.valueOf(dto.getGender()) : null);
        s.setGuardianName(dto.getGuardianName());
        s.setContactPhone(dto.getContactPhone());
        s.setAddress(dto.getAddress());
        s.setSchoolClass(cls);
        studentCredentialRepo.findByRollNumber(oldRollNumber)
        .ifPresentOrElse(
            c -> {
                System.out.println("Credential found");
                c.setRollNumber(dto.getRollNumber());
                studentCredentialRepo.save(c);
            },
            () -> System.out.println("Credential NOT found")
        );
        return toDto(studentRepo.save(s));
    }

    @Transactional
    public void delete(Long id) {

        try {

            Student student = studentRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            studentCredentialRepo.findByRollNumber(student.getRollNumber())
                    .ifPresent(studentCredentialRepo::delete);

            
            marksRepo.deleteByStudentId(id);
            attendanceRepo.deleteByStudentId(id);

            studentRepo.delete(student);

            studentRepo.flush(); // force SQL execution now

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private StudentDto toDto(Student s) {
        StudentDto dto = new StudentDto();
        dto.setId(s.getId());
        dto.setRollNumber(s.getRollNumber());
        dto.setFullName(s.getFullName());
        dto.setDateOfBirth(s.getDateOfBirth());
        dto.setGender(s.getGender() != null ? s.getGender().name() : null);
        dto.setGuardianName(s.getGuardianName());
        dto.setContactPhone(s.getContactPhone());
        dto.setAddress(s.getAddress());
        dto.setClassId(s.getSchoolClass().getId());
        dto.setClassName(s.getSchoolClass().getName());
        dto.setAdmissionDate(s.getAdmissionDate());
        return dto;
    }

    private String generateRandomPassword() {
        int number = (int) (Math.random() * 9000) + 1000;
        return "stu" + number;
    }

    public StudentDto getStudentById(Long id) {

        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return toDto(student);
    }
}
