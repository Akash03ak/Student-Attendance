package com.college.attendance.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.college.attendance.entity.AttendanceEntity;
import com.college.attendance.entity.StudentEntity;
import com.college.attendance.repository.AttendanceRepo;


@Service
public class AttendanceService {
	
	
	@Autowired
	private AttendanceRepo ar;
	
	@Autowired
	private StudentService ss;

	public void saveAttendance(Long classId, int hour,LocalDate date, List<Long> presentId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			throw new RuntimeException("Authentication failed!");
		}
		
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
				|| auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {	
		
		
			List<StudentEntity> stu= ss.getAll(classId);
			
			if(date==null) {
				date=LocalDate.now();
			}
			
			for(StudentEntity s: stu) {
				
				if(ar.existsByHourAndDateAndStudentId(hour, date, s.getId())) {
					throw new RuntimeException("This hour already submited!");
				}
				
				AttendanceEntity attendance=new AttendanceEntity();
				
				attendance.setDate(LocalDate.now());
				attendance.setHour(hour);
				attendance.setStudent(s);
				if(presentId.contains(s.getId())) {
					attendance.setStatus(true);
				}
				else {
					attendance.setStatus(false);
				}
				
				ar.save(attendance);
			}
		}
		else {
			throw new RuntimeException("Authentication failed!");
		}
	}
	
	
	//Get Monthly report
	public List<Object[]> getMonthlyReport(Long studentId){
		return ar.getMonthlyReport(studentId,LocalDate.now().getYear());
	}


	//reset Attendance
	public void deleteById(Long classId) {
		if(classId == null) {
			throw new RuntimeException("Please Select the class!");
		}
		ar.deleteAllAttendanceByClassId(classId);
		System.out.println("welcome to ");
	}

}
