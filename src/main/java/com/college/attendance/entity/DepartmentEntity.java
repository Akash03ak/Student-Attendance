package com.college.attendance.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class DepartmentEntity {

	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String name;

	@OneToMany(mappedBy= "department", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
	private List<CourseClassEnity> classes;
	
	
	//Methods
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CourseClassEnity> getClasses() {
		return classes;
	}
	public void setClasses(List<CourseClassEnity> classes) {
		this.classes = classes;
	}
	
}
