package com.loanflow.dto.response;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SimulatorResponse {
    private BigDecimal amount;
    private BigDecimal monthlyInterestRate; // ← antes era annualInterestRate
    private Integer termMonths;
    private BigDecimal monthlyPayment;
    private BigDecimal totalAmount;
    private BigDecimal totalInterest;
    private List<SimulatorInstallmentResponse> installments;
}
