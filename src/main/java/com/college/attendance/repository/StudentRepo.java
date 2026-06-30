package com.college.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.attendance.entity.StudentEntity;

public interface StudentRepo extends JpaRepository<StudentEntity, Long>{
	
	long count();

	//Return all student on the class
	List<StudentEntity> findByCourseClassEnityIdOrderByRollNoAsc(Long classId);

	//Return student by roll number
	StudentEntity findByRollNo(String rollno);
	
	boolean existsByCourseClassEnityId(Long id);
	
	boolean existsByRollNo(String rollno);
	
	boolean existsByRollNoIgnoreCaseAndNameIgnoreCase(String rollno, String name);

}
