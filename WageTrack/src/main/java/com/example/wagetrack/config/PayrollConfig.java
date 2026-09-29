package com.example.wagetrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PayrollConfig {
    @Value("${wagetrack.payroll.standard-workday-hours:8}")
    private int standardWorkdayHours;
    @Value("${wagetrack.payroll.overtime-multiplier:1.5}")
    private BigDecimal overtimeMultiplier;

    public int getStandardWorkdayHours() { return standardWorkdayHours; }
    public BigDecimal getOvertimeMultiplier() { return overtimeMultiplier; }
}
