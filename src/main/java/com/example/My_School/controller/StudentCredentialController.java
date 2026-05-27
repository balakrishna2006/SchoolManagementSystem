package com.example.My_School.controller;

import com.example.My_School.model.StudentCredential;
import com.example.My_School.repository.StudentCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class StudentCredentialController {

    private final StudentCredentialRepository credentialRepo;

    @GetMapping("/student-credentials/class/{classId}")
    public List<StudentCredential> getCredentials(
            @PathVariable Long classId) {

        return credentialRepo.findBySchoolClassId(classId);
    }
}