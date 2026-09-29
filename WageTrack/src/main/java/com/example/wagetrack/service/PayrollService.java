package com.example.wagetrack.service;

import com.example.wagetrack.config.PayrollConfig;
import com.example.wagetrack.dto.WeeklyPayrollResponse;
import com.example.wagetrack.entity.Attendance;
import com.example.wagetrack.entity.Worker;
import com.example.wagetrack.enums.AttendanceStatus;
import com.example.wagetrack.exception.*;
import com.example.wagetrack.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PayrollService {
    private final WorkerRepository workerRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollConfig config;

    public PayrollService(WorkerRepository w, AttendanceRepository a, PayrollConfig c) {
        this.workerRepository = w; this.attendanceRepository = a; this.config = c;
    }

    public WeeklyPayrollResponse calculate(Long workerId, LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start))
            throw new BusinessRuleException("weekEndDate must be on or after weekStartDate.");
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + workerId));
        List<Attendance> records = attendanceRepository.findByWorkerIdAndAttendanceDateBetween(workerId, start, end);
        long present = 0, half = 0, absent = 0;
        BigDecimal regular = BigDecimal.ZERO, overtime = BigDecimal.ZERO;
        for (Attendance a : records) {
            BigDecimal base = BigDecimal.ZERO;
            if (a.getStatus() == AttendanceStatus.PRESENT) { present++; base = worker.getDailyWage(); }
            else if (a.getStatus() == AttendanceStatus.HALF_DAY) { half++; base = worker.getDailyWage().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP); }
            else { absent++; }
            regular = regular.add(base);
            if (a.getStatus() != AttendanceStatus.ABSENT && a.getOvertimeHours().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal hourly = worker.getDailyWage().divide(BigDecimal.valueOf(config.getStandardWorkdayHours()), 4, RoundingMode.HALF_UP);
                overtime = overtime.add(a.getOvertimeHours().multiply(hourly).multiply(config.getOvertimeMultiplier()));
            }
        }
        regular = regular.setScale(2, RoundingMode.HALF_UP);
        overtime = overtime.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = regular.add(overtime).setScale(2, RoundingMode.HALF_UP);
        return new WeeklyPayrollResponse(worker.getId(), worker.getName(), start, end,
                present, half, absent, regular, overtime, total);
    }
}
