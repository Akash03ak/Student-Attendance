package com.college.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.college.attendance.repository.AnouncementRepo;

@Controller
public class HomeController {
	
	@Autowired
	private AnouncementRepo arepo;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("anouncement", arepo.findAll());
		return "/home";
	}
}
