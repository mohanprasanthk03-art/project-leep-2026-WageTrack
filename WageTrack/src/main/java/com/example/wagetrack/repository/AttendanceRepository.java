package com.example.wagetrack.repository;

import com.example.wagetrack.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByWorkerIdAndAttendanceDate(Long workerId, LocalDate attendanceDate);
    boolean existsByWorkerIdAndAttendanceDateAndIdNot(Long workerId, LocalDate attendanceDate, Long id);
    List<Attendance> findByWorkerIdAndAttendanceDateBetween(Long workerId, LocalDate start, LocalDate end);
}
