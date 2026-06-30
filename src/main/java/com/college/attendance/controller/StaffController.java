package com.college.attendance.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.college.attendance.entity.CourseClassEnity;
import com.college.attendance.entity.StudentEntity;
import com.college.attendance.entity.UserEntity;
import com.college.attendance.repository.CourseClassRepo;
import com.college.attendance.repository.StudentRepo;
import com.college.attendance.repository.UserRepo;
import com.college.attendance.repository.AttendanceRepo;

@Controller
@RequestMapping("/staff")
public class StaffController {

	
	@Autowired
	private CourseClassRepo ccrepo;
	
	@Autowired
	private UserRepo urepo;
	
	@Autowired
	private StudentRepo srepo;
	
	@Autowired
	private AttendanceRepo arepo;
	
	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication auth) {
		
		// 1. Fetch Login User (Staff) Details
		String username = auth.getName();
		UserEntity staff = urepo.findByUsername(username);
		
		model.addAttribute("uname", username);
		model.addAttribute("staffName", staff.getName());
		model.addAttribute("staffEmail", staff.getEmail());
		model.addAttribute("staffRole", staff.getRole().toString());
		
		// Get Staff Assigned Class ID
		Long classId = staff.getClassId();
		model.addAttribute("classId", classId);
		
		String className = "No Class Assigned";
		int totalStudentsCount = 0;
		int todayPresentCount = 0;
		int todayAbsentCount = 0;
		
		List<Map<String, Object>> studentListWithPercentage = new ArrayList<>();
		
		if (classId != null) {
			// Get Class Object for Name Display
			CourseClassEnity classEntity = ccrepo.findById(classId).orElse(null);
			if (classEntity != null) {
				className = classEntity.getCourse()+" "+classEntity.getYear()+"Year"; // Assuming getClassName() exists in your entity
			}
			
			// 2. Fetch All Students in this Class
			List<StudentEntity> students = srepo.findByCourseClassEnityIdOrderByRollNoAsc(classId);
			totalStudentsCount = students.size();
			
			LocalDate today = LocalDate.now();
			
			// Loop and Calculate Individual Statistics
			for (int i = 0; i < students.size(); i++) {
				StudentEntity student = students.get(i);
				Map<String, Object> sMap = new HashMap<>();
				sMap.put("sno", i + 1);
				sMap.put("rollNo", student.getRollNo());
				sMap.put("name", student.getName());
				
				// Attendance Checks from AttendanceRepo
				Long totalHours = arepo.countByStudentId(student.getId());
				Long presentHours = arepo.countByStudentIdAndStatus(student.getId(), true);
				sMap.put("present", presentHours);
				// Calculate Percentage
				int percentage = 0; // Default if no hours logged yet
				if (totalHours > 0) {
					percentage = (int) Math.round((double) presentHours / totalHours * 100);
				}
				sMap.put("percentage", percentage);
				studentListWithPercentage.add(sMap);
				
				// 3. Today Live Attendance Stats (Check if any hour logged for today)
				// Checking first hour status as daily summary baseline indicator or iterate over hours
				List<com.college.attendance.entity.AttendanceEntity> todayLogs = arepo.findByStudentIdAndDate(student.getId(), today);
				if (!todayLogs.isEmpty()) {
					// Check if student was present in the last marked hour or majority hours
					boolean wasPresentToday = todayLogs.get(0).getStatus(); 
					if (wasPresentToday) {
						todayPresentCount++;
					} else {
						todayAbsentCount++;
					}
				} else {
					// Fallback default mock counters if attendance is not taken yet for today
					todayPresentCount = totalStudentsCount;
					todayAbsentCount = 0;
				}
			}
		}
		
		// 4. Period Tracker Setup (Dynamic Data Representation)
		List<Map<String, String>> periodsList = new ArrayList<>();
		LocalDate todayDate = LocalDate.now();
		
		for (int hour = 1; hour <= 5; hour++) {
			Map<String, String> pMap = new HashMap<>();
			pMap.put("name", "P" + hour);
			
			// Check if any student in this class has attendance marked for this hour today
			boolean isPeriodMarked = false;
			if (classId != null && !studentListWithPercentage.isEmpty()) {
				Long sampleStudentId = srepo.findByCourseClassEnityIdOrderByRollNoAsc(classId).get(0).getId();
				isPeriodMarked = arepo.existsByHourAndDateAndStudentId(hour, todayDate, sampleStudentId);
			}
			
			if (isPeriodMarked) {
				pMap.put("status", "Completed");
				pMap.put("cssClass", "bg-green-100 text-green-800");
			} else {
				pMap.put("status", "Pending");
				pMap.put("cssClass", "bg-amber-100 text-amber-800");
			}
			periodsList.add(pMap);
		}
		
		// Thymeleaf Bindings mapping values
		model.addAttribute("classIncharge", className);
		model.addAttribute("totalStudents", totalStudentsCount);
		model.addAttribute("presentToday", todayPresentCount);
		model.addAttribute("absentToday", todayAbsentCount);
		model.addAttribute("periods", periodsList);
		model.addAttribute("students", studentListWithPercentage);
		
		return "staff/dashboard";
	}
	
}
