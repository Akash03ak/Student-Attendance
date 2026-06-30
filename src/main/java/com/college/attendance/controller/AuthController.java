package com.college.attendance.controller;


import java.time.LocalDateTime;
import java.util.Collections;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.college.attendance.entity.StudentEntity;
import com.college.attendance.entity.UserEntity;
import com.college.attendance.repository.StudentRepo;
import com.college.attendance.repository.UserRepo;
import com.college.attendance.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	@Autowired
	UserRepo urepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	StudentRepo srepo;
	
	@Autowired
	private EmailService es;
	
	private static final Logger logger= LoggerFactory.getLogger(AuthController.class);
	///---------------------------------------------------Users login block----------------------------------------
	///
	/// Admin and Staff login page
	///
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request,Model model) {
    	int num1=(int)(Math.random()*10)+1;
    	int num2=(int)(Math.random()*10)+1;
    	int answer= num1+num2;
    	
    	request.getSession().setAttribute("captcha_answer", answer);
    	
    	model.addAttribute("num1", num1);
    	model.addAttribute("num2", num2);
    	
        return "login";
    }

    ///
    ///Redirect dashboard
    ///
    @GetMapping("/redirect-dashboard")
    public String dashboard(Authentication auth) {
        
        if(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        	return "redirect:/admin/dashboard";
        }
        else{
            return "redirect:/staff/dashboard";
        }

    }
    
    ///-----------------------------------------------users password reset block---------------------------------------------------
    ///
    ///1.Return Forgot password page
    ///
    @GetMapping("/forgot-password")
    public String forgotPage() {
    	return "auth/forgot-password";
    }
    
    ///
    ///2.process forgot password
    ///
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("username") String username,
            HttpSession session,
            Model model) {

        UserEntity user = urepo.findByUsername(username);

        // Security Validation Matrix
        if (user == null || user.getEmail() == null) {
            model.addAttribute("error", "Invalid Username or Registered Email address!");
            return "auth/forgot-password";
        }

        // Generate 6-digit random crypto secure OTP token format
        int otpValue = (int)(Math.random()*900000)+100000;

        // Save generated tokens inside current HTTPSession lifecycle context bounds securely
        session.setAttribute("generatedOTP", otpValue);
        session.setAttribute("resetUsername", username);

        // Dispatch raw secure notification email dispatch protocol execution logic
        try {
            String email=user.getEmail();
            String subject="RSGC Security Verification: Password Reset OTP";
            String Text="Hello " + user.getName() + ",\n\nYour 6-digit secure password reset authentication token is: " 
                        + otpValue + "\n\nIf you did not initiate this system account recovery request, secure your logs immediately.";
            es.sendOtp(email,subject, Text);
            session.setAttribute("otpSentSuccess", true);
            model.addAttribute("success", "Secure verification OTP token dispatched directly to your registered email handle!");
            return "redirect:/verify-otp"; // Render structural token entry form screen matrix
            
        }
        catch (Exception ex) {
            model.addAttribute("error", "Registered mail is wrong (or) Mail engine failed to deliver.");
            logger.error("Email sending error: {}", ex);
            return "auth/forgot-password";
        }
    }
    
    ///
    ///3. Render verification interface manually if accessed during tracking sessions
    ///
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session) {
        if (session.getAttribute("generatedOTP") == null) {
            return "redirect:/forgot-password";
        }
        return "auth/verify-otp";
    }
    
    ///
    ///4.Validate user entered data parameters text token against session tokens
    ///
    @PostMapping("/verify-otp")
    public String verifyOtpToken(
            @RequestParam("userOTP") int userOTP,
            HttpSession session,
            Model model) {

        Integer generatedOTP = (Integer) session.getAttribute("generatedOTP");

        if (generatedOTP == null || generatedOTP != userOTP) {
            model.addAttribute("error", "Security verification crash: Invalid or timed out verification OTP parameters token!");
            return "auth/verify-otp";
        }

        // Flag user state inside session token variables as identity cleared verification check passing status
        session.setAttribute("identityVerified", true);
        return "redirect:/reset-password"; // Load safe credential overwrite user interface layout
    }
    
    ///
    ///5. Render Final Password Overwrite endpoint form interface layer rules
    ///
    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session) {
        if (session.getAttribute("identityVerified") == null) {
            return "redirect:/forgot-password";
        }
        return "auth/reset-password";
    }
    
    ///
    ///This is actually reset the password
    /// 6. Overwrite identity parameters records details inside persistent database schema registry
    @PostMapping("/reset-password")
    public String saveNewPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        if (session.getAttribute("identityVerified") == null) {
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Data verification failure: Form input credentials mismatched fields confirmation!");
            return "auth/reset-password";
        }

        String username = (String) session.getAttribute("resetUsername");
        UserEntity user = urepo.findByUsername(username);

        if (user != null) {
            // Encrypt raw textual configurations fields data array with BCrypt dynamic layers
            user.setPassword(passwordEncoder.encode(newPassword));
            urepo.save(user);
            
            // Wipe active operational state boundaries metrics completely clear from dynamic tracking session context loops
            session.invalidate();
            
            model.addAttribute("successPasswordReset", "Master account credential profile flushed successfully! Proceed to sign-in.");
        } else {
            model.addAttribute("error", "Operational crash: Target user context instance lost in database loops execution pipelines!");
        }

        return "auth/reset-password";
    }
    
    
    ///----------------------------------------------------------------Student block--------------------------------------------------------
    ///
    ///return student login page 
    ///
    @GetMapping("/student-login")
    public String studentLoginPage() {
    	return "student-login";
    }
    
    ///
    ///IT send the OTP message to student email and return OTP enter page
    ///
    @PostMapping("/student-send-otp")
    public String sendOTP(@RequestParam String username, Model model) {
    
    	StudentEntity student=srepo.findByRollNo(username);
    	
    	if(student == null) {
    		model.addAttribute("error","Student not found");
    		return "student-login";
    	}
    	
    	String otp=String.valueOf((int)(Math.random()*900000)+100000);
    	
    	student.setOtp(otp);
    	
    	student.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
    	
    	srepo.save(student);
    	try {
    	
    	String subject="RSGC Security Verification: Login OTP";
        String text="Hello " + student.getName() + ",\n\nYour 6-digit secure login authentication token is: "+otp; 
    	es.sendOtp(student.getEmail(),subject,text);
          } catch (Exception ex) {
        	 
        	  model.addAttribute("error", "Registered mail is wrong (or) Mail engine failed to deliver.");
        	  return "student-login";
           }
    	model.addAttribute("rollnumber",username);
    	
    	return "auth/student-verify-otp";
    }
    
    ///
    ///Verify the student OTP and return student dashboard
    ///
    @PostMapping("/student-verify-otp")
    public String verifyOtp(@RequestParam String rollnumber, @RequestParam String otp, HttpSession session) {
    	
    	StudentEntity student= srepo.findByRollNo(rollnumber);
    	
    	boolean validOtp = student.getOtp().equals(otp);
    	
    	boolean notExpried= student.getOtpExpiry().isAfter(LocalDateTime.now());
    	if(validOtp && notExpried) {
    		session.setAttribute("studentId", student.getId());
    		Authentication auth= new UsernamePasswordAuthenticationToken(student, null, Collections.emptyList());
    		SecurityContext context= SecurityContextHolder.createEmptyContext();
    		context.setAuthentication(auth);
    		SecurityContextHolder.setContext(context);
    		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    		
    		return "redirect:/student/dashboard";
    	}
    	return "redirect:/student-login";
    }
    
}