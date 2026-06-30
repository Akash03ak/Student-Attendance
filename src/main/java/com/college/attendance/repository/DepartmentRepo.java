package com.college.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.attendance.entity.DepartmentEntity;

public interface DepartmentRepo extends JpaRepository<DepartmentEntity, Long> {

	//Check is name exists
	boolean existsByName(String name);
	
	long count();
}
