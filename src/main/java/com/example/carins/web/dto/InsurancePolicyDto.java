package com.example.carins.web.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class InsurancePolicyDto {
    private String provider;

    private LocalDate startDate;

    @NotBlank
    @Column(nullable = false)
    private LocalDate endDate;
}

// TODO delete later maybe
