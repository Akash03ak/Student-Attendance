package com.college.attendance.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.attendance.service.AttendanceService;
import com.college.attendance.service.StudentService;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
	
	@Autowired
	private StudentService ss;
	
	@Autowired
	private AttendanceService as;
	
	
	@GetMapping("/mark")
	public String attendanceForm(@RequestParam Long classId,Model model) {
		model.addAttribute("classId", classId);
		model.addAttribute("students", ss.getAll(classId));
		return "class/mark-attendance";
	}

	@PostMapping("/save")
	public String save(@RequestParam Long classId, @RequestParam int hour,@RequestParam(value="date",required= false) LocalDate date,
			@RequestParam(value="present",required= false) List<Long> presentId,
			RedirectAttributes ra) {
		try {
			as.saveAttendance(classId, hour,date, presentId);
			ra.addFlashAttribute("success","Attendance submitted successfully" );
		}
		catch(RuntimeException e) {
			ra.addFlashAttribute("error",e.getMessage() );
		}
		return "redirect:/attendance/mark?classId="+classId;
	}

	
	@PostMapping("/reset")
	public String reset(@RequestParam Long classId,RedirectAttributes ra) {
		try {
			as.deleteById(classId);
			ra.addFlashAttribute("success","Attendance reset successfully!");
			return "redirect:/staff/dashboard";
		}
		catch(Exception e) {
			ra.addFlashAttribute("error","Could not be reset the attendance!");
			return "redirect:/staff/dashboard";
			
		}
	}
}
