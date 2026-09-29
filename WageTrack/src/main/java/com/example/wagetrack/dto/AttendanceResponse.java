package com.example.wagetrack.dto;

import com.example.wagetrack.enums.AttendanceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(Long id, Long workerId, String workerName,
                                 Long worksiteId, String worksiteName,
                                 LocalDate attendanceDate, AttendanceStatus status,
                                 BigDecimal overtimeHours, LocalDateTime createdAt) {}
