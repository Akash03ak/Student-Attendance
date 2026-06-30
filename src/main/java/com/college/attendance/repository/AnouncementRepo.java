package com.college.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.attendance.entity.Anouncement;

public interface AnouncementRepo extends JpaRepository<Anouncement ,Long>{

}
