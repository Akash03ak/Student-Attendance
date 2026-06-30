package com.college.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.college.attendance.entity.AttendanceEntity;

public interface AttendanceRepo extends JpaRepository<AttendanceEntity,Long>{

	boolean existsByHourAndDateAndStudentId(int hour,LocalDate date,Long id);
	
	List<AttendanceEntity> findByStudentIdAndDate(Long studentId,LocalDate date);
	
	Long countByStudentIdAndStatus(Long studentId, boolean status);
	
    Long countByStudentId(Long studentId);
    
    ///
    ///Return Month,totalHours,Absent,Present
    ///
    @Query("""
    		SELECT MONTH(a.date),COUNT(a),
    		SUM(CASE WHEN a.status = true THEN 1 ELSE 0 END),
    		SUM(CASE WHEN a.status = false THEN 1 ELSE 0 END) 
    		FROM AttendanceEntity a WHERE a.student.id =:studentId AND YEAR(a.date) =:year GROUP BY MONTH(a.date) 
    		ORDER BY MONTH(a.date) DESC
    		""")
    List<Object[]> getMonthlyReport(@Param("studentId")Long studentId, @Param("year") int year);
    
    
    ///
    ///Return June month dates 
    ///
    @Query("""
    		SELECT a FROM AttendanceEntity a WHERE a.student.id =:studentId AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date ASC 
    		""")
    List<AttendanceEntity> getDayWiseReport(@Param("studentId") Long studentId,@Param("startDate") LocalDate startDate,
    		@Param("endDate") LocalDate endDate );
    
    
    ///
    ///Delete all attendance of the class(reset operation)
    ///
    @Modifying
    @Transactional
    @Query("DELETE FROM AttendanceEntity a WHERE a.student.courseClassEnity.id=:classId")
    void deleteAllAttendanceByClassId(@Param("classId") Long classId);
}
