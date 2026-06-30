package com.college.attendance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.college.attendance.entity.Role;
import com.college.attendance.entity.UserEntity;
import com.college.attendance.repository.UserRepo;

@Configuration
public class PasswordConfig{

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CommandLineRunner run(UserRepo userRepository,
	                      PasswordEncoder passwordEncoder) {
	    return args -> {

	        // Admin check
	        if (userRepository.findByUsername("admin") == null) {

	            UserEntity admin = new UserEntity();
	            admin.setUsername("admin");
	            admin.setPassword(passwordEncoder.encode("admin123"));
	            admin.setRole(Role.ADMIN);

	            userRepository.save(admin);
	        }

	    };
	}
}
