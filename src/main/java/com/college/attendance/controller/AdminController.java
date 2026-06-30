package com.college.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.attendance.entity.Anouncement;
import com.college.attendance.entity.UserEntity;
import com.college.attendance.repository.AnouncementRepo;
import com.college.attendance.repository.CourseClassRepo;
import com.college.attendance.repository.StudentRepo;
import com.college.attendance.repository.UserRepo;
import com.college.attendance.service.DepartmentService;
import com.college.attendance.service.UserService;


@Controller
@RequestMapping("/admin")
public class AdminController {

	///
	///create repository object 
	///
	@Autowired
	private DepartmentService ds;
	
	@Autowired
	private StudentRepo sp;
	
	@Autowired
	private CourseClassRepo ccp;
	
	@Autowired
	private UserService us;
	
	
	@Autowired
	private UserRepo up;
	
	@Autowired
	private AnouncementRepo arepo;
	
	
	//-----------------------------------------------------------------
	
	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication auth) {
		
		String username = auth.getName();
		UserEntity admin = up.findByUsername(username);

		model.addAttribute("username",username);
		model.addAttribute("name", admin.getName());
		model.addAttribute("email", admin.getEmail());
		
		model.addAttribute("departments", ds.getAll());
		model.addAttribute("deptCount",ds.getCount());
		model.addAttribute("userCount",up.count());
		model.addAttribute("classCount",ccp.count());
		model.addAttribute("studentCount",sp.count());
		return "admin/dashboard";
	}
	
	
		///
		///Users Operations
		///
		
		@GetMapping("/users")
		public String viewUsers(Model model) {
			
			model.addAttribute("users", up.findAll());
			
			return "admin/users";
		}
		
		///
		///Create User
		///
		@GetMapping("/create-user")
		public String createForm(Model model) {
			model.addAttribute("userEntity", new UserEntity());
			model.addAttribute("classes",ccp.findAll());
			return "admin/user-form";
		}
		
		///
		///Edit user
		///
		@GetMapping("/edit-user/{id}")
		public String editUser(@PathVariable long id,Model model) {
			UserEntity ue= up.findById(id).orElseThrow();
			model.addAttribute("userEntity", ue);
			model.addAttribute("classes",ccp.findAll());
			return "admin/user-form";
		}
		
		///
		///save the user
		///
		@PostMapping("/save-user")
		public String saveUser(@ModelAttribute UserEntity ue,RedirectAttributes ra) {
			try {
				ra.addFlashAttribute("success",us.saveUser(ue));
			}
			catch(IllegalArgumentException e) {
				ra.addFlashAttribute("error", e.getMessage());
			}
			return "redirect:/admin/users";
		}
		
		///
		///Delete User
		///
		@PostMapping("/delete-user/{id}")
		public String deleteUser(@PathVariable long id,RedirectAttributes ra) {
			try {
				ra.addFlashAttribute("success", us.deleteUser(id));
			}
			catch(Exception e) {
				ra.addFlashAttribute("error", e.getMessage());
			}
			return "redirect:/admin/users";
		}
		
		
		//-------------------------------------------------------------------------------------------
		///
		///Announcement Operation
		///
		
		@GetMapping("/create-anounce")
		public String anounceForm(Model model) {
			model.addAttribute("announcement", new Anouncement());
			return "admin/anouncement-form";
		}
		
		@GetMapping("/announcement")
		public String anouncement(Model model) {
			
			model.addAttribute("announcement",arepo.findAll());
			return "admin/announcement";
		}
		
		@PostMapping("/save-anounce")
		public String saveAnounce(@ModelAttribute Anouncement anounce,RedirectAttributes ra) {
			try {
				arepo.save(anounce);
				ra.addFlashAttribute("success", "Announcement saved successfully!");
			}
			catch(Exception e) {
				ra.addFlashAttribute("error", e.getMessage());
			}
				return "redirect:/admin/announcement";
		}
		
		
		@PostMapping("/delete-anounce/{id}")
		public String deleteAnounce(@PathVariable long id,RedirectAttributes ra) {
			try {
				arepo.deleteById(id);
				ra.addFlashAttribute("success", "Announcement deleted successfully!");
			}
			catch(Exception e) {
				ra.addFlashAttribute("error", e.getMessage());
			}
			return "redirect:/admin/announcement";
		}
}
