package com.example.My_School.service;

import com.example.My_School.model.Student;
import com.example.My_School.model.User;
import com.example.My_School.repository.StudentRepository;
import com.example.My_School.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // First check admin/staff users
        var user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return user.get();
        }

        // Then check student table using roll number
        var student = studentRepository
                .findByRollNumberAndActiveTrue(username);

        if (student.isPresent()) {
            Student s = student.get();

            return User.builder()
                    .id(-s.getId())
                    .username(s.getRollNumber())
                    .password(s.getLoginPassword())
                    .fullName(s.getFullName())
                    .role(User.Role.STUDENT)
                    .schoolClass(s.getSchoolClass())
                    .studentId(s.getId())
                    .active(true)
                    .build();
        }

        throw new UsernameNotFoundException(
                "User not found: " + username);
    }
}