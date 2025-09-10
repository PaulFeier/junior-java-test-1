package com.example.carins.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class InsuranceClaimRequestDto {
    @NotNull(message = "Claim date is required")
    @PastOrPresent(message = "Claim date must be in the past or present")
    private LocalDate claimDate;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private double amount;

    // Getters and setters
    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
