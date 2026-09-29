package com.example.wagetrack;

import com.example.wagetrack.config.PayrollConfig;
import com.example.wagetrack.entity.Attendance;
import com.example.wagetrack.entity.Worker;
import com.example.wagetrack.enums.AttendanceStatus;
import com.example.wagetrack.repository.AttendanceRepository;
import com.example.wagetrack.repository.WorkerRepository;
import com.example.wagetrack.service.PayrollService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayrollCalculationTest {
    @Test
    void calculatesQuestionPaperExample() {
        WorkerRepository workers = mock(WorkerRepository.class);
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        PayrollConfig config = new PayrollConfig();
        // Configuration is normally injected by Spring; reflection is avoided by testing
        // the published 8-hour/1.5x assumptions with a small test subclass not needed here.
        org.springframework.test.util.ReflectionTestUtils.setField(config, "standardWorkdayHours", 8);
        org.springframework.test.util.ReflectionTestUtils.setField(config, "overtimeMultiplier", new BigDecimal("1.5"));

        Worker worker = new Worker();
        worker.setWorkerCode("W001"); worker.setName("Ravi"); worker.setPhone("9876543210");
        worker.setDailyWage(new BigDecimal("800.00"));
        when(workers.findById(1L)).thenReturn(Optional.of(worker));
        when(attendance.findByWorkerIdAndAttendanceDateBetween(eq(1L), any(), any())).thenReturn(
                List.of(row( worker, AttendanceStatus.PRESENT, "2"),
                        row(worker, AttendanceStatus.PRESENT, "0"),
                        row(worker, AttendanceStatus.HALF_DAY, "0"),
                        row(worker, AttendanceStatus.ABSENT, "0"),
                        row(worker, AttendanceStatus.PRESENT, "1"),
                        row(worker, AttendanceStatus.PRESENT, "0"),
                        row(worker, AttendanceStatus.PRESENT, "0")));
        PayrollService service = new PayrollService(workers, attendance, config);
        var result = service.calculate(1L, LocalDate.parse("2026-09-21"), LocalDate.parse("2026-09-27"));
        assertEquals(new BigDecimal("4400.00"), result.regularWage());
        assertEquals(new BigDecimal("450.00"), result.overtimePay());
        assertEquals(new BigDecimal("4850.00"), result.totalPayable());
    }

    private Attendance row(Worker worker, AttendanceStatus status, String overtime) {
        Attendance a = new Attendance(); a.setWorker(worker); a.setStatus(status);
        a.setOvertimeHours(new BigDecimal(overtime)); return a;
    }
}
