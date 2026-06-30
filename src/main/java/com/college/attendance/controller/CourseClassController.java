package com.college.attendance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.attendance.entity.CourseClassEnity;
import com.college.attendance.entity.DepartmentEntity;
import com.college.attendance.entity.StudentEntity;
import com.college.attendance.service.CourseClassService;
import com.college.attendance.service.DepartmentService;
import com.college.attendance.service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/classes")
public class CourseClassController {

	@Autowired
	private CourseClassService ccs;
	
	@Autowired
	private DepartmentService ds;
	
	@Autowired
	private StudentService ss;
	
	
	//Get All classes in the department
	@GetMapping
	public String getClassesByDeptId(@RequestParam Long departmentId, Model model) {
		model.addAttribute("classes", ccs.getClassesByDeptId(departmentId));
		model.addAttribute("department",ds.getById(departmentId));
		return "class/classes";
	}
	
	
	//return new class form
	@GetMapping("/new")
	public String createForm(@RequestParam Long departmentId,Model model) {
		model.addAttribute("department", ds.getById(departmentId));
		model.addAttribute("classEntity",new CourseClassEnity());
		return "class/add-class";
	}
	
	
	@GetMapping("/edit")
	public String editClass(@RequestParam Long classId,Model model) {
		CourseClassEnity cce=ccs.getById(classId);
		model.addAttribute("department", cce.getDepartment());
		model.addAttribute("classEntity",cce);
		return "class/add-class";
	}
	
	
	//save new class
	@PostMapping("/save")
	public String save(@RequestParam Long departmentId, @ModelAttribute CourseClassEnity cls, RedirectAttributes rda) {
		
		DepartmentEntity dept = ds.getById(departmentId);		
		cls.setDepartment(dept);
		try {
			ccs.save(cls);
			rda.addFlashAttribute("success","Saved Successfully!");
		}
		catch(IllegalArgumentException iae) {
			rda.addFlashAttribute("error",iae.getMessage());
		}
		catch(Exception e) {
			rda.addFlashAttribute("error",e.getMessage());
			return "class/add-class";
		}
		
		return "redirect:/classes?departmentId="+departmentId;
	}
	
	//Delete class by id
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes rda) {
		CourseClassEnity cce=ccs.getById(id);
		
		try {
			ccs.delete(id);
			rda.addFlashAttribute("success","Deleted Successfully!");
		}
		catch(Exception e) {
			rda.addFlashAttribute("error",e.getMessage());
		}
		return "redirect:/classes?departmentId="+cce.getDepartment().getId();
	}
	
	
	//---------------=======================----------------------==========================-----------------------
	
	//Return all student of the class
	@GetMapping("/students")
	public String getStudents(@RequestParam Long classId,Model model) {
		List<StudentEntity> s= ss.getAll(classId);
		model.addAttribute("student", s);
		model.addAttribute("class", ccs.getById(classId));
		
		return "class/students";
	}
	
	//create student form
	@GetMapping("/students/create")
	public String studentForm(@RequestParam Long classId, Model model,HttpServletResponse res) {
		model.addAttribute("student", new StudentEntity());
		model.addAttribute("classId",classId);
		res.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);

		
		return "class/add-student";
	}
	
	//save student
	@PostMapping("/students/save")
	public String saveStudent(@ModelAttribute StudentEntity student, @RequestParam Long classId, RedirectAttributes ra) {
		
		CourseClassEnity cce= ccs.getById(classId);
		
		student.setCourseClassEnity(cce);
		
		try {
			ss.save(student);
			ra.addFlashAttribute("success", "Student saved successfully!");
		}
		catch(IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		catch(Exception e){
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/classes/students?classId="+classId;
	}
	
	
	//edit student
	@GetMapping("/students/edit")
	public String editStudent(@RequestParam Long studentId,Model model,HttpServletResponse res) {
		model.addAttribute("student",ss.getById(studentId));
		model.addAttribute("classId",ss.getById(studentId).getCourseClassEnity().getId());
		
		res.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		return "class/add-student";
	}
	
	//delete student
	@PostMapping("/students/delete")
	public String deleteStudent(@RequestParam Long id, RedirectAttributes ra) {
		Long classId= ss.getById(id).getCourseClassEnity().getId();
		try {
			ss.delete(id);
			ra.addFlashAttribute("success", "Delete student successfully!");
		}
		catch(Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/classes/students?classId="+classId;
	}
	
}
