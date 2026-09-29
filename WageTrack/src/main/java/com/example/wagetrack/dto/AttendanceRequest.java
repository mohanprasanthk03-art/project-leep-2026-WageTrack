package com.example.wagetrack.dto;

import com.example.wagetrack.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AttendanceRequest(
    @NotNull @Positive Long workerId,
    @NotNull @Positive Long worksiteId,
    @NotNull LocalDate attendanceDate,
    @NotNull AttendanceStatus status,
    @NotNull @PositiveOrZero BigDecimal overtimeHours
) {}
