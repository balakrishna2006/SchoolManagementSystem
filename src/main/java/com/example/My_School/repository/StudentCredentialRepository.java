package com.example.My_School.repository;

import com.example.My_School.model.StudentCredential;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCredentialRepository
        extends JpaRepository<StudentCredential, Long> {
            List<StudentCredential> findBySchoolClassId(Long classId);
            Optional<StudentCredential> findByRollNumber(String rollNumber);
}