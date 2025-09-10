package com.example.carins.web.dto;

import com.example.carins.model.Car;

import java.time.LocalDate;

public record InsurancePolicyDto(Long id, Car carId, String provider, LocalDate startDate, LocalDate endDate) {}
