package com.college.attendance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.college.attendance.entity.DepartmentEntity;
import com.college.attendance.repository.CourseClassRepo;
import com.college.attendance.repository.DepartmentRepo;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentRepo deptRepo;
	
	@Autowired
	private CourseClassRepo ccr;
	
	//return all department
	public List<DepartmentEntity> getAll(){
		return deptRepo.findAll();
	}
	
	
	///
	///Add new Department
	///
	public DepartmentEntity save(DepartmentEntity dept) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {	

			//check if department name is null it throw an Exception
			if(dept.getName()==null || dept.getName().isBlank()) {
				throw new RuntimeException("Department name not to be null");
			}
	
			//check the department is already exists
			if(deptRepo.existsByName(dept.getName())) {
				throw new RuntimeException("Department Already exists");
			}
			
			return deptRepo.save(dept);
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
	
	
	///
	///return department by id
	///
	public DepartmentEntity getById(Long id) {
		
		//check Department is exists if not throw an Exception
		if(!deptRepo.existsById(id)) {
			throw new RuntimeException("Department Not Found");
		}
		
		return deptRepo.findById(id).orElse(null);
	}
	
	
	
	///
	///delete department by id
	///
	public void delete(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {	

			//Find department if not throw exception
			deptRepo.findById(id).orElseThrow(() -> new RuntimeException("Department Not Found"));
			
			if(ccr.existsByDepartmentId(id)) {
				throw new RuntimeException("Cannot delete. Department has classes!");
			}
					
			deptRepo.deleteById(id);
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
	
	///
	///Get count of the departments
	///
	public Long getCount() {
		return deptRepo.count();
	}
}
