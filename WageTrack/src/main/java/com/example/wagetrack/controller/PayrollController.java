package com.example.wagetrack.controller;

import com.example.wagetrack.dto.WeeklyPayrollResponse;
import com.example.wagetrack.service.PayrollService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    private final PayrollService service;
    public PayrollController(PayrollService service) { this.service = service; }

    @GetMapping("/worker/{workerId}/weekly")
    public WeeklyPayrollResponse weekly(@PathVariable Long workerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndDate) {
        return service.calculate(workerId, weekStartDate, weekEndDate);
    }
}
