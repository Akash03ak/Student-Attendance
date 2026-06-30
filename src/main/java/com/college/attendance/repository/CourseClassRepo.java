package com.college.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.attendance.entity.CourseClassEnity;
import com.college.attendance.entity.CourseClassEnity.Shift;

public interface CourseClassRepo extends JpaRepository<CourseClassEnity, Long>{

	List<CourseClassEnity> findByDepartmentId(Long departmentId);
	
	boolean existsByDepartmentId(Long id);
	
	boolean existsByCourseIgnoreCaseAndYearAndShiftAndDepartmentId(
			String couse, int year, Shift shift,Long id);
	
	long count();
}
