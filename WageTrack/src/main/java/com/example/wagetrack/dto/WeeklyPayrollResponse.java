package com.example.wagetrack.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeeklyPayrollResponse(Long workerId, String workerName,
                                    LocalDate weekStartDate, LocalDate weekEndDate,
                                    long presentDays, long halfDays, long absentDays,
                                    BigDecimal regularWage, BigDecimal overtimePay,
                                    BigDecimal totalPayable) {}
