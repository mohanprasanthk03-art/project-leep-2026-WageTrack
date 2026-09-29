package com.example.wagetrack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkerResponse(Long id, String workerCode, String name, String phone,
                             String address, BigDecimal dailyWage, boolean active,
                             LocalDateTime createdAt) {}
