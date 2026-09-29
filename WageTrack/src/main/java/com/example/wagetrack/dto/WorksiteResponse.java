package com.example.wagetrack.dto;

public record WorksiteResponse(Long id, String siteCode, String siteName,
                               String location, String description, boolean active) {}
