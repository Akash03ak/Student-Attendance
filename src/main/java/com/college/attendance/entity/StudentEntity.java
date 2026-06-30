package com.college.attendance.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class StudentEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(unique=true,nullable=false)
	private String rollNo;
	private String name;
	@Column(unique= true)
	private String email;
	
	private String otp;
	private LocalDateTime otpExpiry;
	
	@ManyToOne
	@JoinColumn(name="courseId")
	private CourseClassEnity courseClassEnity;//This variable name only used on JPA operation not courseId
	
	@OneToMany(mappedBy="student", cascade = CascadeType.ALL,orphanRemoval=true, fetch= FetchType.LAZY)
	private List<AttendanceEntity> attendanceList;
	
	//method Getter setter
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRollNo() {
		return rollNo;
	}
	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CourseClassEnity getCourseClassEnity() {
		return courseClassEnity;
	}
	public void setCourseClassEnity(CourseClassEnity courseClassEnity) {
		this.courseClassEnity = courseClassEnity;
	}
	public List<AttendanceEntity> getAttendanceList() {
		return attendanceList;
	}
	public void setAttendanceList(List<AttendanceEntity> attendanceList) {
		this.attendanceList = attendanceList;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public LocalDateTime getOtpExpiry() {
		return otpExpiry;
	}
	public void setOtpExpiry(LocalDateTime otpExpiry) {
		this.otpExpiry = otpExpiry;
	}
	
	
	
}
