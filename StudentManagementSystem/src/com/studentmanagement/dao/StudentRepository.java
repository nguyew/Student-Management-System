package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import java.util.List;

public interface StudentRepository extends CrudRepository<Student, String>{
    List<Student> searchByKeyword (String keyword);
    List<Student> findByClassName (String className);
    List<Student> findByMajor (String major);
    List<Student> findByGPARange (double minGpa, double maxGpa);
    List<Student> findByGender (String gender);
    List<Student> findTopStudentsByGpa (int limit);
    java.util.Map<String , Object> getAcademicStatistics();
}
