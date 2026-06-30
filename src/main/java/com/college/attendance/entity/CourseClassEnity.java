package com.college.attendance.entity;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class CourseClassEnity {
	
	public enum Shift{
		MORNING,EVENING
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String course; //MSC,BSC
	private int year; //1,2,3
	
	@Enumerated(EnumType.STRING)
	private Shift shift; //MORNING / EVENING
	
	@ManyToOne
	@JoinColumn(name = "departmentId")
	private DepartmentEntity department;
	
	
	//cascade- parent action affect child(eg:- if department deleted, department based all classes are deleted)
	//fetch - when data loads (lazy- not now)
	@OneToMany(mappedBy="courseClassEnity", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
	private List<StudentEntity> students;
	

	//Methods Getter and Setter
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public DepartmentEntity getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentEntity department) {
		this.department = department;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}
	
	
}
