package com.loanflow.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class SimulatorRequest {
    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal monthlyInterestRate; // cualquier valor: 1.5, 2, 3.5, etc.

    @NotNull
    private Integer termMonths;
}
