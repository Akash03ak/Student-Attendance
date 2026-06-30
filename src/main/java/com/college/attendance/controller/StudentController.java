package com.college.attendance.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.college.attendance.dto.DayWiseReport;
import com.college.attendance.entity.AttendanceEntity;
import com.college.attendance.entity.StudentEntity;
import com.college.attendance.repository.AttendanceRepo;
import com.college.attendance.repository.StudentRepo;
import com.college.attendance.service.AttendanceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	StudentRepo srepo;
	
	@Autowired
	AttendanceRepo arepo;
	
	@Autowired
	private AttendanceService as;
	

	@GetMapping("/dashboard")
	public String dashboard(@RequestParam(name="id",required=false) Long id,HttpSession session,Model model) {
		
		Long studentId=null;
		
		try {
		    studentId= (Long) session.getAttribute("studentId");
		}
		catch(IllegalStateException  e) {}
		
		if(studentId==null || studentId==0) {
			if(id==null || id==0) {
				return "redirect:/student-login";
			}
			else {
			studentId=id;
			}
		}
		
		StudentEntity student= srepo.findById(studentId).orElseThrow();
		List<AttendanceEntity> attedance= arepo.findByStudentIdAndDate(studentId, LocalDate.now());
		Map<Integer,Boolean> map=new HashMap<>();
		for(AttendanceEntity a : attedance) {
			map.put(a.getHour(), a.getStatus());
		}
		Long totalhours= arepo.countByStudentId(studentId);
		Long presentHours= arepo.countByStudentIdAndStatus(studentId, true);
		double percentage=0;
		if(totalhours >0) {
			percentage= (presentHours * 100.0) / totalhours;
		}
		String status,progress;
		if(percentage > 75) {
			status=" Good Standing";
			progress="success";
		}
		else if(percentage > 60) {
			status  =" Need Improvement";
			progress= "warning";
		}
		else {
			status=" Attendance Shortage";
			progress="danger";
		}
		
		List<Object[]> report= as.getMonthlyReport(studentId);
		
		String monthsName[]= {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
		
		model.addAttribute("student", student);
		model.addAttribute("monthsName", monthsName);
		model.addAttribute("attendance",map);
		model.addAttribute("percentage",Math.round(percentage));
		model.addAttribute("presenthours",presentHours);
		model.addAttribute("absenthours",totalhours-presentHours);
		model.addAttribute("totalhours", totalhours);
		model.addAttribute("progressColor", progress);
		model.addAttribute("status",status);
		model.addAttribute("report", report);
		
		return "student/dashboard";
	}
	
	
	@GetMapping("/day-wise-report")
	public String dayWiseReport(@RequestParam(name="id",required=false) Long id,@RequestParam Integer month, HttpSession session,Model model) {
		
		//Get student id form session context
Long studentId=null;
		
		try {
		    studentId= (Long) session.getAttribute("studentId");
		}
		catch(IllegalStateException  e) {}
		
		if(studentId==null || studentId==0) {
			if(id==null || id==0) {
				return "redirect:/student-login";
			}
			else {
			studentId=id;
			}
		}
		
		//make selected month start date(1) and end date
		YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(),month);
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();
		
		//it fetch the date form database month attendance 
		List<AttendanceEntity> daywiselist = arepo.getDayWiseReport(studentId, startDate, endDate);
		
		//calculate the total,present,and absent hours 
		int totalHours= daywiselist.size();
		long presentHours= daywiselist.stream().filter(AttendanceEntity::getStatus).count();
		//this filter() method filter the status only form AttendanceEntity
		long absentHours = totalHours - presentHours;
		//Calculate the month attendance percentage
		int percentage = totalHours > 0? (int) Math.round(((double) presentHours/totalHours)*100):0;
		
		//it map all hours on the day to single row
		Map<LocalDate,DayWiseReport> matrixMap= new LinkedHashMap<>();
		for(AttendanceEntity attendance: daywiselist) {
			LocalDate date= attendance.getDate();
			matrixMap.putIfAbsent(date, new DayWiseReport(date));//if date is absent DaywiseReport class is set up.
			matrixMap.get(date).addHourStatus(attendance.getHour(), attendance.getStatus());
		}
		
		@SuppressWarnings("unused")
		StudentEntity student= srepo.findById(studentId).orElseThrow();
		model.addAttribute("studentName", student.getName());
		model.addAttribute("className",student.getCourseClassEnity().getCourse().toUpperCase()+" "+
		student.getCourseClassEnity().getYear()+"YEAR");
		model.addAttribute("deptName", student.getCourseClassEnity().getDepartment().getName());
		model.addAttribute("daywise", matrixMap.values());//Day wise list		
		model.addAttribute("month", yearMonth.getMonth().name()+LocalDate.now().getYear());//month name and year
		model.addAttribute("workDays", matrixMap.size());
		model.addAttribute("totalhours", totalHours);
		model.addAttribute("present", presentHours);
		model.addAttribute("absent",absentHours);
		model.addAttribute("percentage", percentage);
		return "student/day-wise-report";
	}
}
