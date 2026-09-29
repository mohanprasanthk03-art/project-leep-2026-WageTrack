package com.example.wagetrack.dto;

import com.example.wagetrack.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(Long id, Long workerId, String workerName,
                              LocalDate weekStartDate, LocalDate weekEndDate,
                              BigDecimal payableAmount, BigDecimal amountPaid,
                              LocalDate paymentDate, PaymentStatus paymentStatus,
                              String remarks) {}
