package com.example.wagetrack.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record WorkerRequest(
    @NotBlank @Size(max = 30) String workerCode,
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must contain 10 to 15 digits, optionally starting with +") String phone,
    @Size(max = 500) String address,
    @NotNull @Positive BigDecimal dailyWage,
    Boolean active
) {}
