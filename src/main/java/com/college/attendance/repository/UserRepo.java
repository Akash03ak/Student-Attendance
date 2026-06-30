package com.college.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.attendance.entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity,Long>{
	
	long count();

	UserEntity findByUsername(String username);
	
	//exists the username
	boolean existsByUsernameIgnoreCase(String username);

}
