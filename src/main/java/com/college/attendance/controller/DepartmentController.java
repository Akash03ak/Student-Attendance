package com.college.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.attendance.entity.DepartmentEntity;
import com.college.attendance.service.DepartmentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

	
	@Autowired
	private DepartmentService deptService;
	
	///
	///Return the departments
	///
	@GetMapping
	public String listDepartments(Model model) {
		model.addAttribute("departments", deptService.getAll());
		return "department/departments";
	}
	
	
	///
	///Return the form for create new department
	///
	@GetMapping("/new")
	public String createForm(Model model,HttpServletResponse response) {
		
		model.addAttribute("department",new DepartmentEntity());
		
		return "department/add-department";
	}
	
	///
	///Edit department
	///
	@GetMapping("/edit/{id}")
	public String editDepartment(@PathVariable Long id, Model model,HttpServletResponse response) {
		DepartmentEntity dept= deptService.getById(id);
		model.addAttribute("department", dept);
		return "department/add-department";
	}
	
	
	///
	///Save and Update Department
	///
	@PostMapping("/save")
	public String saveDepartment(@ModelAttribute DepartmentEntity dept, RedirectAttributes rda,Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth==null) {
			model.addAttribute("error", "Authentication Failed!");
			return "department/add-department";
		}
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			try {
				deptService.save(dept);
				rda.addFlashAttribute("success","Saved Successfully!");
			}
			catch(RuntimeException e) {
				model.addAttribute("department",dept);
				model.addAttribute("error", e.getMessage());
				return "department/add-department";
				
			}
			
			return "redirect:/departments";
		}
		else {
			model.addAttribute("error", "Authentication Failed!");
			return "department/add-department";			
		}
	}
	
	
	///
	///Delete department
	///
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes rda,HttpServletResponse response) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth==null) {
			rda.addFlashAttribute("error", "Authentication Failed!");
			return "department/add-department";
		}
		if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

			try {
				deptService.delete(id);
				rda.addFlashAttribute("success","Deleted Successfully!");
			}
			catch(RuntimeException e) {
				rda.addFlashAttribute("error", e.getMessage());
			}
			
			return "redirect:/departments";
		}
		else {
			rda.addFlashAttribute("error","Authentication failed!");
			return "department/add-department";			
		}
		
		
	}
}
