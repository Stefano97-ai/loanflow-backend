package com.loanflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequest {

    private Long userId;

    private Long loanTypeId;

    @NotNull @DecimalMin("100.00")
    private BigDecimal amount;

    @NotNull @Min(1)
    private Integer termMonths;

    private String purpose;

    @DecimalMin(value = "0.01", message = "La tasa de interés mensual debe ser mayor a 0")
    @DecimalMax(value = "30.00", message = "La tasa de interés mensual no puede superar el 30%")
    private BigDecimal tasaInteresPersonalizada;
}
