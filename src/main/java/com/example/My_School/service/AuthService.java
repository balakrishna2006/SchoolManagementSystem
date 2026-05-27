package com.example.My_School.service;

import com.example.My_School.dto.AuthRequest;
import com.example.My_School.dto.AuthResponse;
import com.example.My_School.model.Student;
import com.example.My_School.model.User;
import com.example.My_School.repository.StudentRepository;
import com.example.My_School.repository.UserRepository;
import com.example.My_School.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Unified login:
     * - Admin / Staff → looks up users table (existing behaviour)
     * - Student → looks up students table by roll_number + login_password
     */
    public AuthResponse login(AuthRequest request) {

        // 1. Try student login first (roll_number as username)
        Optional<Student> studentOpt = studentRepository.findByRollNumberAndActiveTrue(request.getUsername());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getLoginPassword() == null ||
                    !passwordEncoder.matches(request.getPassword(), student.getLoginPassword())) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Invalid username or password");
            }
            // Build a synthetic User principal for JWT
            User synthetic = User.builder()
                    .id(-student.getId()) // negative id signals STUDENT
                    .username(student.getRollNumber())
                    .password(student.getLoginPassword())
                    .fullName(student.getFullName())
                    .role(User.Role.STUDENT)
                    .schoolClass(student.getSchoolClass())
                    .studentId(student.getId())
                    .active(true)
                    .build();

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "STUDENT");
            claims.put("studentId", student.getRollNumber());
            claims.put("classId", student.getSchoolClass().getId());

            String token = jwtService.generateToken(synthetic, claims);

            return AuthResponse.builder()
                    .token(token)
                    .role("STUDENT")
                    .fullName(student.getFullName())
                    .userId(synthetic.getId())
                    .classId(student.getSchoolClass().getId())
                    .studentId(student.getRollNumber())
                    .build();
        }

        // 2. Admin / Staff login (existing logic)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),
                        request.getPassword()));
        User user = (User) auth.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        String token = jwtService.generateToken(user, claims);

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .userId(user.getId())
                .classId(user.getSchoolClass() != null ? user.getSchoolClass().getId() : null)
                .studentId(null)
                .build();
    }

    /** Staff can reset a student's password */
    public void resetStudentPassword(Long studentId, String newPassword) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        student.setLoginPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
    }
}