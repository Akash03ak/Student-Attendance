package com.college.attendance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.college.attendance.entity.CourseClassEnity;
import com.college.attendance.repository.CourseClassRepo;
import com.college.attendance.repository.StudentRepo;

@Service
public class CourseClassService {

	@Autowired
	private CourseClassRepo ccRepo;
	
	@Autowired
	private StudentRepo srepo;
	
	///
	///Return all classes on department by department id
	///
	public List<CourseClassEnity> getClassesByDeptId(Long id){
		return ccRepo.findByDepartmentId(id);
	}
	
	///
	///Save Class
	///
	public CourseClassEnity save(CourseClassEnity c){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {	
		
			//It checks unique constraints of course, year, and shift. 
			boolean exists= ccRepo.existsByCourseIgnoreCaseAndYearAndShiftAndDepartmentId(
					c.getCourse(), c.getYear(), c.getShift(), c.getDepartment().getId());
			
			if(exists) {
				throw new IllegalArgumentException("Class Already exists");
			}
			
			return ccRepo.save(c);
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
	
	///
	///Return class by id
	///
	public CourseClassEnity getById(Long id) {
				return ccRepo.findById(id).orElseThrow(()-> new RuntimeException("Class not found"));
	}
	
	///
	///Delete class by id
	///
	public void delete(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {	

			if(!ccRepo.existsById(id)) {
				throw new RuntimeException("Class not found");
			}
			if(srepo.existsByCourseClassEnityId(id)) {
				throw new RuntimeException("Cannot delete, Class has students!");
			}
			ccRepo.deleteById(id);
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
}
