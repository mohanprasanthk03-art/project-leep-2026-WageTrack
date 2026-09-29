package com.example.wagetrack.dto;

import jakarta.validation.constraints.*;

public record WorksiteRequest(
    @NotBlank @Size(max = 30) String siteCode,
    @NotBlank @Size(max = 120) String siteName,
    @NotBlank @Size(max = 200) String location,
    @Size(max = 500) String description,
    Boolean active
) {}
