package com.example.My_School.repository;

import com.example.My_School.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
 
public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findBySchoolClassIdAndAcademicYear(Long classId, String academicYear);
    Optional<Marks> findByStudentIdAndExamTypeAndAcademicYear(
            Long studentId, Marks.ExamType examType, String academicYear);
 
    @Query("SELECT m FROM Marks m WHERE m.student.id = :studentId AND m.academicYear = :year")
    List<Marks> findAllByStudentAndYear(Long studentId, String year);

    void deleteByStudentId(Long studentId);
}
