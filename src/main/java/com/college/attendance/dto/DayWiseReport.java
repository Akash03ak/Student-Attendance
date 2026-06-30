package com.college.attendance.dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DayWiseReport {
	
	private LocalDate date;
	private Map<Integer,Boolean> hourStatus= new HashMap<>();
	
	public DayWiseReport(LocalDate date) {
		this.date=date;
	}
	
	public LocalDate getDate() {
		return date;
	}
	public Map<Integer,Boolean> getHourStatus(){
		return hourStatus;
			
	}
	public void addHourStatus(int hour,boolean status) {
		this.hourStatus.put(hour, status);
	}
	

}
