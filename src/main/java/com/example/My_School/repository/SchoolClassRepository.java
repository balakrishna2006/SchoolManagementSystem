package com.example.My_School.repository;

import com.example.My_School.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllByOrderByGradeAsc();
}
