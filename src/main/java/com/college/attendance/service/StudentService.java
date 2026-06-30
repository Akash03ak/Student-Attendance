package com.college.attendance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.college.attendance.entity.StudentEntity;
import com.college.attendance.repository.StudentRepo;

@Service
public class StudentService {

	@Autowired
	private StudentRepo srepo;
	
	
	//Return all student in the class
	public List<StudentEntity> getAll(long id){
		return srepo.findByCourseClassEnityIdOrderByRollNoAsc(id);
		
	}
	
	//save the student
	public StudentEntity save(StudentEntity s) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
				|| auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {	
		
			if(s.getName().isEmpty() && s.getRollNo().isEmpty()) {
				throw new RuntimeException("All feilds must be required");
			}
			if(srepo.existsByRollNo(s.getRollNo())) {
				throw new RuntimeException("Student roll number: " + s.getRollNo() +" already exists!");
			}
			return srepo.save(s);
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
	
	//Return student by id
	public StudentEntity getById(Long id) {
		return srepo.findById(id).orElse(null);
	}
	
	//Delete Student by id
	public void delete(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
				|| auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {	

			srepo.deleteById(id);
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
}
