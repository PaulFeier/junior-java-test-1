package com.example.carins.web.dto;

import java.time.LocalDate;

public record CarHistoryEventDto(LocalDate date, String type, String description) {}
