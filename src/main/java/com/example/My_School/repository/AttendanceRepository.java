package com.example.My_School.repository;

import com.example.My_School.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySchoolClassIdAndAttDate(Long classId, LocalDate date);
    Optional<Attendance> findByStudentIdAndAttDate(Long studentId, LocalDate date);
 
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT'")
    long countPresentByStudent(Long studentId);
 
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalByStudent(Long studentId);


    void deleteByStudentId(Long studentId);
}
