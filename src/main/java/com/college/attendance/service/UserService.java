package com.college.attendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.college.attendance.entity.UserEntity;
import com.college.attendance.repository.UserRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo urepo;
	
	@Autowired
	private PasswordEncoder pe;
	
	
	//Save user
	public String saveUser(UserEntity ue) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {	
		
			//new user
			if(ue.getId()==null && urepo.existsByUsernameIgnoreCase(ue.getUsername())) {
				throw new IllegalArgumentException("User already exists.");
			}
			else if(ue.getId()==null){
				ue.setPassword(pe.encode(ue.getPassword()));
				urepo.save(ue);	
				return "User saved successfully!";
			}
			
			//edit user
			UserEntity ucheck= urepo.findById(ue.getId()).orElseThrow();
			if(!ue.getUsername().equalsIgnoreCase(ucheck.getUsername()) && urepo.existsByUsernameIgnoreCase(ue.getUsername()) ) {
				throw new IllegalArgumentException("User already exists.");
			}
			urepo.save(ue);
			
			return "User edited successfully!";
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
	
	//delete user
	public String deleteUser(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {	

			urepo.deleteById(id);
			return "User delete successfully!";
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
}
