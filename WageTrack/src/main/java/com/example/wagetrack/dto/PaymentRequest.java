package com.example.wagetrack.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(
    @NotNull @Positive Long workerId,
    @NotNull LocalDate weekStartDate,
    @NotNull LocalDate weekEndDate,
    @NotNull @PositiveOrZero BigDecimal amountPaid,
    LocalDate paymentDate,
    @Size(max = 500) String remarks
) {}
