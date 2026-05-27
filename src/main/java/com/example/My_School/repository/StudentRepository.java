package com.example.My_School.repository;

import com.example.My_School.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchoolClassIdAndActiveTrue(Long classId);
    boolean existsByRollNumber(String rollNumber);
    Optional<Student> findByRollNumberAndActiveTrue(String rollNumber);
}